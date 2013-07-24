// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf;

import java.util.List;
import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Matcher;
import org.kew.shs.dedupl.matchconf.Transformer;
import org.kew.shs.dedupl.matchconf.Wire;

privileged aspect Wire_Roo_JavaBean {
    
    public String Wire.getSourceColumnName() {
        return this.sourceColumnName;
    }
    
    public void Wire.setSourceColumnName(String sourceColumnName) {
        this.sourceColumnName = sourceColumnName;
    }
    
    public String Wire.getLookupColumnName() {
        return this.lookupColumnName;
    }
    
    public void Wire.setLookupColumnName(String lookupColumnName) {
        this.lookupColumnName = lookupColumnName;
    }
    
    public Boolean Wire.getUseInSelect() {
        return this.useInSelect;
    }
    
    public void Wire.setUseInSelect(Boolean useInSelect) {
        this.useInSelect = useInSelect;
    }
    
    public Boolean Wire.getUseInNegativeSelect() {
        return this.useInNegativeSelect;
    }
    
    public void Wire.setUseInNegativeSelect(Boolean useInNegativeSelect) {
        this.useInNegativeSelect = useInNegativeSelect;
    }
    
    public Boolean Wire.getIndexLength() {
        return this.indexLength;
    }
    
    public void Wire.setIndexLength(Boolean indexLength) {
        this.indexLength = indexLength;
    }
    
    public Boolean Wire.getBlanksMatch() {
        return this.blanksMatch;
    }
    
    public void Wire.setBlanksMatch(Boolean blanksMatch) {
        this.blanksMatch = blanksMatch;
    }
    
    public Boolean Wire.getIndexOriginal() {
        return this.indexOriginal;
    }
    
    public void Wire.setIndexOriginal(Boolean indexOriginal) {
        this.indexOriginal = indexOriginal;
    }
    
    public Boolean Wire.getIndexInitial() {
        return this.indexInitial;
    }
    
    public void Wire.setIndexInitial(Boolean indexInitial) {
        this.indexInitial = indexInitial;
    }
    
    public Boolean Wire.getUseWildcard() {
        return this.useWildcard;
    }
    
    public void Wire.setUseWildcard(Boolean useWildcard) {
        this.useWildcard = useWildcard;
    }
    
    public Matcher Wire.getMatcher() {
        return this.matcher;
    }
    
    public void Wire.setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }
    
    public Configuration Wire.getConfiguration() {
        return this.configuration;
    }
    
    public void Wire.setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    public List<Transformer> Wire.getSourceTransformers() {
        return this.sourceTransformers;
    }
    
    public void Wire.setSourceTransformers(List<Transformer> sourceTransformers) {
        this.sourceTransformers = sourceTransformers;
    }
    
    public List<Transformer> Wire.getLookupTransformers() {
        return this.lookupTransformers;
    }
    
    public void Wire.setLookupTransformers(List<Transformer> lookupTransformers) {
        this.lookupTransformers = lookupTransformers;
    }
    
}