package org.kew.shs.dedupl.lucene;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.shs.dedupl.DataLoader;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.reporters.LuceneReporter;
import org.kew.shs.dedupl.reporters.Reporter;


public class LuceneHandler {

	private org.apache.lucene.util.Version luceneVersion;

	protected FSDirectory directory;
	protected IndexWriter indexWriter;
	private Analyzer analyzer;
	protected IndexReader indexReader;
	private QueryParser queryParser;
	protected DataLoader dataLoader;
	protected Configuration config;
	protected Logger log = Logger.getLogger(this.getClass());

	protected LuceneReporter[] reporters;

	public LuceneHandler() {
		super();
	}

	public Configuration getConfig() {
		return this.config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public DataLoader getDataLoader() {
		return dataLoader;
	}

	public void setDataLoader(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

	public Document getFromLucene(int n) throws IOException {
		return indexReader.document(n);
	}

	public TopDocs queryLucene(String query, IndexSearcher indexSearcher) throws IOException,
			ParseException {
				log.debug(query);
				Query q = queryParser.parse(query);
				log.debug(q);
				return indexSearcher.search(q, 1000);
			}

	public org.apache.lucene.util.Version getLuceneVersion() {
		return luceneVersion;
	}

	public void setLuceneVersion(org.apache.lucene.util.Version luceneVersion) {
		this.luceneVersion = luceneVersion;
	}

	public FSDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(FSDirectory directory) {
		this.directory = directory;
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public IndexReader getIndexReader() {
		return indexReader;
	}

	public void setIndexReader(IndexReader indexReader) {
		this.indexReader = indexReader;
	}

	public QueryParser getQueryParser() {
		return queryParser;
	}

	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}

	public Reporter[] getReporters() {
		return (LuceneReporter[]) reporters;
	}

	public void setReporters(LuceneReporter[] reporters) {
		this.reporters = reporters;
	}



}