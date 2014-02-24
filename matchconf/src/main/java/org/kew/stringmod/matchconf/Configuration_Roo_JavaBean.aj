// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.stringmod.matchconf;

import java.util.List;
import org.kew.stringmod.matchconf.Configuration;
import org.kew.stringmod.matchconf.Matcher;
import org.kew.stringmod.matchconf.Reporter;
import org.kew.stringmod.matchconf.Transformer;
import org.kew.stringmod.matchconf.Wire;

privileged aspect Configuration_Roo_JavaBean {
    
    public String Configuration.getName() {
        return this.name;
    }
    
    public void Configuration.setName(String name) {
        this.name = name;
    }
    
    public String Configuration.getWorkDirPath() {
        return this.workDirPath;
    }
    
    public void Configuration.setWorkDirPath(String workDirPath) {
        this.workDirPath = workDirPath;
    }
    
    public String Configuration.getSourceFileName() {
        return this.sourceFileName;
    }
    
    public void Configuration.setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
    
    public String Configuration.getSourceFileEncoding() {
        return this.sourceFileEncoding;
    }
    
    public void Configuration.setSourceFileEncoding(String sourceFileEncoding) {
        this.sourceFileEncoding = sourceFileEncoding;
    }
    
    public String Configuration.getSourceFileDelimiter() {
        return this.sourceFileDelimiter;
    }
    
    public void Configuration.setSourceFileDelimiter(String sourceFileDelimiter) {
        this.sourceFileDelimiter = sourceFileDelimiter;
    }
    
    public String Configuration.getRecordFilter() {
        return this.recordFilter;
    }
    
    public void Configuration.setRecordFilter(String recordFilter) {
        this.recordFilter = recordFilter;
    }
    
    public String Configuration.getNextConfig() {
        return this.nextConfig;
    }
    
    public void Configuration.setNextConfig(String nextConfig) {
        this.nextConfig = nextConfig;
    }
    
    public String Configuration.getLookupFileName() {
        return this.lookupFileName;
    }
    
    public void Configuration.setLookupFileName(String lookupFileName) {
        this.lookupFileName = lookupFileName;
    }
    
    public String Configuration.getLookupFileEncoding() {
        return this.lookupFileEncoding;
    }
    
    public void Configuration.setLookupFileEncoding(String lookupFileEncoding) {
        this.lookupFileEncoding = lookupFileEncoding;
    }
    
    public String Configuration.getLookupFileDelimiter() {
        return this.lookupFileDelimiter;
    }
    
    public void Configuration.setLookupFileDelimiter(String lookupFileDelimiter) {
        this.lookupFileDelimiter = lookupFileDelimiter;
    }
    
    public String Configuration.getPackageName() {
        return this.packageName;
    }
    
    public void Configuration.setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String Configuration.getClassName() {
        return this.className;
    }
    
    public void Configuration.setClassName(String className) {
        this.className = className;
    }
    
    public String Configuration.getLoadReportFrequency() {
        return this.loadReportFrequency;
    }
    
    public void Configuration.setLoadReportFrequency(String loadReportFrequency) {
        this.loadReportFrequency = loadReportFrequency;
    }
    
    public String Configuration.getAssessReportFrequency() {
        return this.assessReportFrequency;
    }
    
    public void Configuration.setAssessReportFrequency(String assessReportFrequency) {
        this.assessReportFrequency = assessReportFrequency;
    }
    
    public String Configuration.getScoreFieldName() {
        return this.scoreFieldName;
    }
    
    public void Configuration.setScoreFieldName(String scoreFieldName) {
        this.scoreFieldName = scoreFieldName;
    }
    
    public String Configuration.getMaxSearchResults() {
        return this.maxSearchResults;
    }
    
    public void Configuration.setMaxSearchResults(String maxSearchResults) {
        this.maxSearchResults = maxSearchResults;
    }
    
    public List<Wire> Configuration.getWiring() {
        return this.wiring;
    }
    
    public void Configuration.setWiring(List<Wire> wiring) {
        this.wiring = wiring;
    }
    
    public List<Transformer> Configuration.getTransformers() {
        return this.transformers;
    }
    
    public void Configuration.setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }
    
    public List<Matcher> Configuration.getMatchers() {
        return this.matchers;
    }
    
    public void Configuration.setMatchers(List<Matcher> matchers) {
        this.matchers = matchers;
    }
    
    public List<Reporter> Configuration.getReporters() {
        return this.reporters;
    }
    
    public void Configuration.setReporters(List<Reporter> reporters) {
        this.reporters = reporters;
    }
    
}