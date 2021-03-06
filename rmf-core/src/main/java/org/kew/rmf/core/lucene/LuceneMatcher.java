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
package org.kew.rmf.core.lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.rmf.core.DataHandler;
import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.configuration.MatchConfiguration;
import org.kew.rmf.core.configuration.Property;
import org.kew.rmf.core.exception.DataLoadException;
import org.kew.rmf.core.exception.MatchExecutionException;
import org.kew.rmf.core.exception.TooManyMatchesException;
import org.kew.rmf.matchers.MatchException;
import org.kew.rmf.reporters.LuceneReporter;
import org.kew.rmf.reporters.Piper;
import org.kew.rmf.transformers.TransformationException;
import org.kew.rmf.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Performs the actual match against the Lucene index.
 *
 * {@link #getMatches(Map, int)} returns a list of matches.
 */
public class LuceneMatcher extends LuceneHandler<MatchConfiguration> implements DataHandler<MatchConfiguration> {
	private static final Logger logger = LoggerFactory.getLogger(LuceneMatcher.class);

    protected MatchConfiguration matchConfig;

    long count = 0;
    long time = System.currentTimeMillis();

    @Override // from DataHandler
	public void loadData() throws DataLoadException, InterruptedException {
		this.dataLoader.setConfig(this.getConfig());
		this.dataLoader.load();
	}

    /**
     * Performs a match against the Lucene index, and returns a list of matches.
     * @param record The record needing to be matched
     * @return A list of matched records
     * @throws TooManyMatchesException Thrown if more than configured maximum number of matches are found
     * @throws MatchExecutionException For other errors finding a match
     */
    public List<Map<String,String>> getMatches(Map<String, String> record) throws TooManyMatchesException, MatchExecutionException {
        if (++count % 1000 == 0) {
            logger.warn("{} match queries processed in {}ms", count, System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        }

        // pipe everything through to the output where an existing filter evaluates to false;
        try {
            if (!StringUtils.isBlank(config.getRecordFilter()) && !jsEnv.evalFilter(config.getRecordFilter(), record)) {
                logger.debug("All records excluded by record filter");
                return new ArrayList<>();
            }
        }
        catch (ScriptException e) {
            throw new MatchExecutionException("Error evaluating recordFilter on record "+record, e);
        }

        // transform query properties
        for (Property prop:config.getProperties()) {
            String fName = prop.getQueryColumnName();
            String fValue = record.get(fName);
            // transform the field-value..
            fValue = fValue == null ? "" : fValue; // super-csv treats blank as null, we don't for now
            for (Transformer t:prop.getQueryTransformers()) {
                try {
                    fValue = t.transform(fValue);
                }
                catch (TransformationException e) {
                    throw new MatchExecutionException("Error evaluating transformer "+t+" on record "+record, e);
                }
            }
            // ..and put it into the record
            record.put(fName + Configuration.TRANSFORMED_SUFFIX, fValue);
        }

        // Use the properties to select a set of documents which may contain matches
        String fromId = record.get(Configuration.ID_FIELD_NAME);
        String querystr = LuceneUtils.buildQuery(config.getProperties(), record, false);
        // If the query for some reasons results being empty we pipe the record directly through to the output
        if (querystr.equals("")) {
            logger.warn("Empty query for record {}", record);
            return new ArrayList<>();
        }

        // Perform the Lucene query
        TopDocs td;
        try {
            td = queryLucene(querystr, this.getIndexSearcher(), config.getMaxSearchResults());
            if (td.totalHits >= config.getMaxSearchResults()) {
                logger.info("Error querying Lucene with {} ({}/{})", querystr, td.totalHits, config.getMaxSearchResults());
                //throw new TooManyMatchesException(String.format("%d potential results (maximum is %d) returned for record %s! You should either tweak your config to bring back less possible results making better use of the \"useInSelect\" switch (recommended) or raise the \"maxSearchResults\" number.", td.totalHits, config.getMaxSearchResults(), record));
                return null;
            }
            logger.debug("Found {} possible record to assess against {}", td.totalHits, fromId);
        }
        catch (ParseException | IOException e) {
            throw new MatchExecutionException("Error querying Lucene on query "+record, e);
        }

        // Run the detailed match against the possibilities returned by Lucene
        List<Map<String, String>> matches = new ArrayList<>();

        for (ScoreDoc sd : td.scoreDocs){
            try {
                Document toDoc = getFromLucene(sd.doc);
                if (LuceneUtils.recordsMatch(record, toDoc, config.getProperties())) {
                    Map<String,String> toDocAsMap = LuceneUtils.doc2Map(toDoc);

                    double matchScore = LuceneUtils.recordsMatchScore(record, toDoc, config.getProperties());
                    toDocAsMap.put(Configuration.MATCH_SCORE, String.format("%5.4f", matchScore));

                    matches.add(toDocAsMap);
                    logger.info("Match is {}", toDocAsMap);
                }
            }
            catch (TransformationException e) {
                throw new MatchExecutionException("Error evaluating transformer while scoring record "+record, e);
            }
            catch (MatchException e) {
                throw new MatchExecutionException("Error running matcher for "+record, e);
            }
            catch (IOException e) {
                throw new MatchExecutionException("Error retrieving match result from Lucene "+sd.doc, e);
            }
        }
        sortMatches(matches);
        return matches;
    }

    public void sortMatches(List<Map<String, String>> matches) {
        final String sortOn = config.getSortFieldName();
        try {
            Collections.sort(matches, Collections.reverseOrder(new Comparator<Map<String, String>>() {
                @Override
                public int compare(final Map<String, String> m1,final Map<String, String> m2) {
                    return Integer.valueOf(m1.get(sortOn)).compareTo(Integer.valueOf(m2.get(sortOn)));
                }
            }));
        } catch (NumberFormatException e) {
            // if the String can't be converted to an integer we do String comparison
            Collections.sort(matches, Collections.reverseOrder(new Comparator<Map<String, String>>() {
                @Override
                public int compare(final Map<String, String> m1,final Map<String, String> m2) {
                    return m1.get(sortOn).compareTo(m2.get(sortOn));
                }
            }));
        }
    }

    /**
     * Run the whole matching task.
     *
     * The iterative flow is:
     * - load the data (== write the lucene index)
     * - iterate over the query data file
     *     - for each record, look for matches in the index
     *    - for each record, report into new fields of this record about matches via reporters
     *
     * The main difference to a deduplication task as defined by {@link LuceneDeduplicator}
     * is that we use two different datasets, one to create the authority index, the other one as
     * query file (where we iterate over each record to look up possible matches).
     */
    @Override
    public void run() throws Exception {

        this.loadData(); // writes the index according to the configuration

        char delimiter = this.config.getQueryFileDelimiter().charAt(0);
        char quote = this.config.getQueryFileQuoteChar().charAt(0);
        CsvPreference csvPref = new CsvPreference.Builder(quote, delimiter, "\n").build();
        logger.info("Reading CSV from {} with delimiter '{}' and quote '{}'", this.getConfig().getQueryFile(), delimiter, quote);

        int i = 0;
        try (MatchConfiguration config = this.getConfig();
            IndexReader indexReader= this.getIndexReader();
            CsvMapReader mr = new CsvMapReader(new InputStreamReader(new FileInputStream(this.getConfig().getQueryFile()), this.getConfig().getQueryFileEncoding()), csvPref)) {

            this.prepareEnvs();

            // loop over the queryFile
            int numMatches = 0;
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertyQueryColumnNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("%s: Header doesn't contain field name < %s > as defined in config.", this.config.getQueryFile().getPath(), name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("%s: Id field name not found in header, should be %s!", this.config.getQueryFile().getPath(), idFieldName));
            Map<String, String> record;
            while((record = mr.read(header)) != null) {
                List<Map<String, String>> matches = getMatches(record);
                if (matches == null) {
                    for (Piper piper:config.getPipers()) piper.pipe(record);
                    continue;
                }
                numMatches += matches.size();
                if (i++ % config.getAssessReportFrequency() == 0) logger.info("Assessed " + i + " records, found " + numMatches + " matches");
                // call each reporter that has a say; all they get is a complete list of duplicates for this record.
                for (LuceneReporter reporter : config.getReporters()) {
                    // TODO: make idFieldName configurable, but not on reporter level
                    reporter.report(record, matches);
                }
            }
            logger.info("Assessed " + i + " records, found " + numMatches + " matches");
        }
    }
}
