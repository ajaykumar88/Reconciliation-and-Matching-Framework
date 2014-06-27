package org.kew.reconciliation.ws;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.query.Query;
import org.kew.reconciliation.refine.domain.response.QueryResponse;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:/META-INF/spring/cucumber.xml")
public class GeneralReconciliationServiceStepdefs extends WebMvcConfigurationSupport {
	private static Logger log = LoggerFactory.getLogger(GeneralReconciliationServiceStepdefs.class);

	private static final String JSON_UTF8_S = "application/json;charset=UTF-8";
	private static final MediaType JSON_UTF8 = MediaType.parseMediaType(JSON_UTF8_S);

	@Autowired
	private WebApplicationContext wac;

	private ObjectMapper mapper = new ObjectMapper();
	private MockMvc mockMvc;

	private String responseJson;

	private MvcResult result;

	public GeneralReconciliationServiceStepdefs() {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@When("^I query for metadata of the \"(.*?)\" reconciliation service$")
	public void i_query_for_metadata_of_the_reconciliation_service(String configName) throws Throwable {
		// Call
		result = mockMvc.perform(get("/reconcile/"+configName).accept(JSON_UTF8))
//				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_UTF8_S))
				.andReturn();

		if (result.getResponse().getStatus() == HttpStatus.OK.value()) {
			responseJson = result.getResponse().getContentAsString();
			log.debug("Response as string was {}", responseJson);
		}
	}

	@Then("^I receive an HTTP (\\d+) result\\.$")
	public void i_receive_an_HTTP_result(int expectedStatus) throws Throwable {
		log.error("expected {} actual {}", expectedStatus, result.getResponse().getStatus());
		Assert.assertThat("Expected HTTP Status "+expectedStatus, result.getResponse().getStatus(), Matchers.equalTo(expectedStatus));
	}

	@Then("^I receive the following metadata response:$")
	public void i_receive_the_following_metadata_response(String expectedResponseString) throws Throwable {
		Metadata responseMetadata = mapper.readValue(responseJson, Metadata.class);

		// Check response
		log.info("Received response {}", responseMetadata);

		Metadata expectedResponse = new ObjectMapper().readValue(expectedResponseString, Metadata.class);
		Assert.assertThat("Metadata response correct", responseMetadata, Matchers.equalTo(expectedResponse));
	}

	@When("^I make a match query for \"(.*?)\"$")
	public void i_make_a_match_query_for(String queryString) throws Throwable {
		// Call
		result = mockMvc.perform(get("/match/generalTest?"+queryString).accept(JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_UTF8_S))
				.andReturn();

		responseJson = result.getResponse().getContentAsString();
		log.debug("Response as string was {}", responseJson);
	}

	@Then("^I receive the following match response:$")
	public void i_receive_the_following_match_response(String expectedJson) throws Throwable {
		JSONAssert.assertEquals(expectedJson, responseJson, true);
	}

	@When("^I make the reconciliation query:$")
	public void i_make_the_reconciliation_query(String queryJson) throws Throwable {
		Query query = mapper.readValue(queryJson, Query.class);

		log.debug("Query is {}", "/reconcile/generalTest?query="+mapper.writeValueAsString(query));

		result = mockMvc.perform(post("/reconcile/generalTest?query={query}", mapper.writeValueAsString(query)).accept(JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_UTF8_S))
				.andReturn();

		responseJson = result.getResponse().getContentAsString();
		log.debug("Response as string was {}", responseJson);
	}

	@Then("^I receive the following reconciliation response:$")
	public void i_receive_the_following_reconciliation_response(String expectedResponseString) throws Throwable {
		QueryResponse actualResponse = mapper.readValue(responseJson, QueryResponse.class);

		// Check response
		log.info("Received response {}", actualResponse);

		QueryResponse expectedResponse = mapper.readValue(expectedResponseString, QueryResponse.class);
		Assert.assertThat("QueryResponse response correct", actualResponse, Matchers.equalTo(expectedResponse));
	}

	@When("^I make the reconciliation queries:$")
	public void i_make_the_reconciliation_queries(String queriesJson) throws Throwable {
		Map<String,Query> queries = mapper.readValue(queriesJson, new TypeReference<Map<String,Query>>() {});

		log.debug("Query is {}", "/reconcile/generalTest?queries="+mapper.writeValueAsString(queries));

		result = mockMvc.perform(post("/reconcile/generalTest?queries={queries}", mapper.writeValueAsString(queries)).accept(JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_UTF8_S))
				.andReturn();

		responseJson = result.getResponse().getContentAsString();
		log.debug("Response as string was {}", responseJson);
	}

	@Then("^I receive the following reconciliation multiple response:$")
	public void i_receive_the_following_reconciliation_multiple_response(String expectedResponseString) throws Throwable {
		Map<String,QueryResponse> actualResponse = mapper.readValue(responseJson, new TypeReference<Map<String,QueryResponse>>() {});

		// Check response
		log.info("Received response {}", actualResponse);

		Map<String,QueryResponse> expectedResponse = mapper.readValue(expectedResponseString, new TypeReference<Map<String,QueryResponse>>() {});
		Assert.assertThat("QueryResponse multiple response correct", actualResponse, Matchers.equalTo(expectedResponse));
	}
}
