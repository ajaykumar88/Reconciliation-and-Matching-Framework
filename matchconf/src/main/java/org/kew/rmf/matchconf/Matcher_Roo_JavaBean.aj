// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.rmf.matchconf;

import java.util.List;

privileged aspect Matcher_Roo_JavaBean {
    
    public String Matcher.getName() {
        return this.name;
    }
    
    public void Matcher.setName(String name) {
        this.name = name;
    }
    
    public String Matcher.getPackageName() {
        return this.packageName;
    }
    
    public void Matcher.setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String Matcher.getClassName() {
        return this.className;
    }
    
    public void Matcher.setClassName(String className) {
        this.className = className;
    }
    
    public String Matcher.getParams() {
        return this.params;
    }
    
    public void Matcher.setParams(String params) {
        this.params = params;
    }
    
    public List<Matcher> Matcher.getComposedBy() {
        return this.composedBy;
    }
    
    public void Matcher.setComposedBy(List<Matcher> composedBy) {
        this.composedBy = composedBy;
    }
    
    public Configuration Matcher.getConfiguration() {
        return this.configuration;
    }
    
    public void Matcher.setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
}
