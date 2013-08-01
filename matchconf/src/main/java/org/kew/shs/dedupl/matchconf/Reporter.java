package org.kew.shs.dedupl.matchconf;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"config", "name"}))
public class Reporter {

    private String name;

    private String delimiter = "&#09;";
    private String idDelimiter = "|";
    private String fileName;

    private String packageName = "org.kew.shs.dedupl.reporters";
    private String className = "LuceneOutputReporter";

    private String params;

    @ManyToOne
    private Configuration config;

    public String toString() {
        return this.name;
    }

    public Reporter clone(Configuration config) {
        Reporter reporter = new Reporter();
        // first the string attributes and manytoones
        reporter.setName(this.name);
        reporter.setPackageName(this.packageName);
        reporter.setClassName(this.className);
        reporter.setParams(this.params);
        reporter.setConfig(config);
        reporter.persist();
        reporter.merge();
        return reporter;
    }

}