/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reconciliation.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.kew.rmf.core.configuration.ReconciliationServiceConfiguration;
import org.kew.rmf.core.exception.MatchExecutionException;
import org.kew.rmf.core.exception.TooManyMatchesException;
import org.kew.rmf.core.lucene.LuceneMatcher;
import org.kew.rmf.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.rmf.refine.domain.query.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:/META-INF/spring/cucumber.xml")
public class MatchConfigurationTestingStepdefs {
	private final static Logger logger = LoggerFactory.getLogger(MatchConfigurationTestingStepdefs.class);

	@Autowired
	private GenericWebApplicationContext wac;

	LuceneMatcher currentMatcher;
	List<Map<String,String>> queryResults;

	@Given("^I have loaded the \"(.*?)\" configuration$")
	public void i_have_loaded_the_configuration(String fileName) throws Throwable {
		currentMatcher = getConfiguration(fileName);
		Assert.assertNotNull("Failed to load matcher", currentMatcher);
	}


	@When("^I query for$")
	public void i_query_for(List<Map<String,String>> queries) throws Throwable {
		Assert.assertNotNull("No matcher selected", currentMatcher);

		// Go through the list of queries one at a time, execute the query, store the result in a new key "result".

		queryResults = new ArrayList<>();

		for (Map<String,String> testQuery : queries) {
			Map<String,String> result = doSingleTestQuery(testQuery);
			logger.debug("Query result: {}", result);
			queryResults.add(result);
		}
	}

	@When("^I query with only a single string for$")
	public void i_query_with_only_a_single_string_for(List<Map<String,String>> singleStringQueries) throws Throwable {
		Assert.assertNotNull("No matcher selected", currentMatcher);

		// Go through the list of queries one at a time, execute the query, store the result in a new key "result".

		queryResults = new ArrayList<>();

		for (Map<String,String> testQuery : singleStringQueries) {
			Map<String,String> result = doSingleStringTestQuery(testQuery.get("queryId"), testQuery.get("queryString"));
			logger.debug("Query result: {}", result);
			queryResults.add(result);
		}
	}

	@Then("^the results are$")
	public void the_results_are(List<Map<String,String>> expectedResults) throws Throwable {
		for (Map<String,String> expectedResult : expectedResults) {
			checkResult(expectedResult);
		}
	}

	private void checkResult(Map<String,String> expectedResult) {
		String targetId = expectedResult.get("queryId");

		Map<String,String> actualResult = null;
		for (Map<String,String> result : queryResults) {
			if (targetId.equals(result.get("queryId"))) {
				actualResult = result;
				break;
			}
		}

		Assert.assertNotNull("No result found for query "+targetId, actualResult);

		Assert.assertThat("Match results not correct for query "+expectedResult.get("queryId"), actualResult.get("results"), Matchers.equalTo(expectedResult.get("results")));
	}

	private Map<String,String> doSingleStringTestQuery(String queryId, String query) throws TooManyMatchesException, MatchExecutionException {
		ReconciliationServiceConfiguration reconConfig = (ReconciliationServiceConfiguration) currentMatcher.getConfig();
		QueryStringToPropertiesExtractor propertiesExtractor = reconConfig.getQueryStringToPropertiesExtractor();

		Property[] properties = propertiesExtractor.extractProperties(query);

		Map<String, String> suppliedRecord = new HashMap<String, String>();
		suppliedRecord.put("queryId", queryId);

		for (Property p : properties) {
			logger.trace("Setting: {} to {}", p.getPid(), p.getV());
			suppliedRecord.put(p.getPid(), p.getV());
		}

		return doSingleTestQuery(suppliedRecord);
	}

	private Map<String,String> doSingleTestQuery(Map<String,String> origQuery) throws TooManyMatchesException, MatchExecutionException {
		// Copy the map, as Cucumber supplies an UnmodifiableMap
		Map<String,String> query = new HashMap<>();
		query.putAll(origQuery);

		query.put("id", query.get("queryId")); // Means the id can show up in the debug output.
		List<Map<String,String>> matches = currentMatcher.getMatches(query);

		logger.debug("Found some matches: {}", matches.size());
		if (matches.size() < 4) {
			logger.debug("Matches for {} are {}", query, matches);
		}

		// Construct result string (list of ids)
		ArrayList<String> matchedIds = new ArrayList<>();
		for (Map<String,String> match : matches) {
			matchedIds.add(match.get("id"));
		}
		Collections.sort(matchedIds);

		query.put("results", StringUtils.join(matchedIds, " "));

		return query;
	}

	private static final Map<String, LuceneMatcher> matchers = new HashMap<String, LuceneMatcher>();

	private LuceneMatcher getConfiguration(String config) throws Throwable {
		logger.debug("Considering initialising match controller with configuration {}", config);

		// Load up the matchers from the specified files
		if (!matchers.containsKey(config)){
			String configurationFile = "/META-INF/spring/reconciliation-service/" + config + ".xml";
			logger.debug("Loading configuration {} from {}", config, configurationFile);
			ConfigurableApplicationContext context = new GenericXmlApplicationContext(configurationFile);
			LuceneMatcher matcher = context.getBean("engine", LuceneMatcher.class);
			matcher.loadData();
			logger.debug("Loaded data for configuration {}", config);
			matchers.put(config, matcher);
			logger.debug("Stored matcher with name {} from configuration {}", matcher.getConfig().getName(), config);
		}

		return matchers.get(config);
	}
}
