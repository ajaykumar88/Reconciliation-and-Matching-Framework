package org.kew.shs.dedupl.matchconf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "sourceColumnName", "lookupColumnName"}))
public class Wire implements Comparable<Wire> {

    private String sourceColumnName;
    private String lookupColumnName = "";

    private Boolean useInSelect = false;

    private Boolean useInNegativeSelect = false;

    private Boolean indexLength = false;

    private Boolean blanksMatch = false;

    private Boolean indexOriginal = false;

    private Boolean indexInitial = false;

    private Boolean useWildcard = false;

    @ManyToOne
    private Matcher matcher;

    @ManyToOne
    private Configuration configuration;

    @ManyToMany
    @Size(min=0)
    @Sort(type=SortType.NATURAL)
    private List<Transformer> sourceTransformers = new ArrayList<Transformer>();

    @ManyToMany
    @Size(min=0)
    private List<Transformer> lookupTransformers = new ArrayList<Transformer>();

    public String getName() {
        return this.getSourceColumnName() + "_" + this.getLookupColumnName();
    }
    @Override
    public int compareTo(Wire w) {
        return this.getName().compareTo(w.getName());
    }

    public String toString() {
        return this.getName();
    }

}