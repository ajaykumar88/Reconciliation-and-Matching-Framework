package org.kew.shs.dedupl.matchconf;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findConfigurationsByNameEquals" })
public class Configuration {

    private String name;

    private String workDirPath;

    private String sourceFileName = "source.tsv";
    private String sourceFileEncoding = "UTF8";
    private String sourceFileDelimiter = "&#09;";

    // lookupFileName being populated decides over being a MatchConfig
    private String lookupFileName = "";
    private String lookupFileEncoding = "UTF8";
    private String lookupFileDelimiter = "&#09;";

    private String outputFileDelimiter = "&#09;";
    private String outputFileIdDelimiter = "|";
    private String outputFileNameExtension = "tsv";

    private String packageName = "org.kew.shs.dedupl.configuration";
    private String className = "DeduplicationConfiguration";

    private String loadReportFrequency = "50000";

    private String assessReportFrequency = "100";

    private String scoreFieldName = "id";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @Sort(type=SortType.NATURAL)
    private List<Wire> wiring = new ArrayList<Wire>();

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Transformer> transformers = new ArrayList<>();

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Matcher> matchers = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void cleanAllPaths() {
        // TODO: change the following local setting to a real local setting :-)
        String cleanedPath = this.getWorkDirPath().replaceAll("\\\\", "/").replaceAll("T:", "/mnt/t_drive");
        this.setWorkDirPath(cleanedPath);
    }

    public static TypedQuery<Configuration> findDedupConfigsByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.name = :name AND o.lookupFileName = :lookupFileName", Configuration.class);
        q.setParameter("name", name);
        q.setParameter("lookupFileName", "");
        return q;
    }

    public static TypedQuery<Configuration> findMatchConfigsByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.name = :name AND o.lookupFileName != :lookupFileName", Configuration.class);
        q.setParameter("name", name);
        q.setParameter("lookupFileName", "");
        return q;
    }

    public static long countDedupConfigs() {
        TypedQuery<Long> q = entityManager().createQuery("SELECT COUNT(o) FROM Configuration AS o WHERE o.lookupFileName = :lookupFileName", Long.class);
        q.setParameter("lookupFileName", "");
        return q.getSingleResult();
    }

    public static long countMatchConfigs() {
        TypedQuery<Long> q = entityManager().createQuery("SELECT COUNT(o) FROM Configuration AS o WHERE o.lookupFileName != :lookupFileName", Long.class);
        q.setParameter("lookupFileName", "");
        return q.getSingleResult();
    }

    public static List<Configuration> findAllDedupConfigs() {
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.lookupFileName = :lookupFileName", Configuration.class);
        q.setParameter("lookupFileName", "");
        return q.getResultList();
    }

    public static List<Configuration> findAllMatchConfigs() {
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.lookupFileName != :lookupFileName", Configuration.class);
        q.setParameter("lookupFileName", "");
        return q.getResultList();
    }

    public static List<Configuration> findDedupConfigEntries(int firstResult, int maxResults) {
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.lookupFileName = :lookupFileName", Configuration.class);
        q.setParameter("lookupFileName", "");
        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Configuration> findMatchConfigEntries(int firstResult, int maxResults) {
        EntityManager em = Configuration.entityManager();
        TypedQuery<Configuration> q = em.createQuery("SELECT o FROM Configuration AS o WHERE o.lookupFileName != :lookupFileName", Configuration.class);
        q.setParameter("lookupFileName", "");
        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public Wire getWireForName(String wireName) {
        for (Wire wire:this.getWiring()) {
            if (wire.getName().equals(wireName)) return wire;
        }
        return null;
    }

    public Transformer getTransformerForName(String transformerName) {
        for (Transformer transformer:this.getTransformers()) {
            if (transformer.getName().equals(transformerName)) return transformer;
        }
        return null;
    }

    public Matcher getMatcherForName(String matcherName) {
        for (Matcher matcher:this.getMatchers()) {
            if (matcher.getName().equals(matcherName)) return matcher;
        }
        return null;
    }

    public void removeWire(String wireName) {
        this.getWiring().remove(this.getWireForName(wireName));
        this.merge();
    }

    public void removeTransformer(String transformerName) {
        this.getTransformers().remove(this.getTransformerForName(transformerName));
        this.merge();
    }

    public void removeMatcher(String matcherName) {
        this.getMatchers().remove(this.getMatcherForName(matcherName));
        this.merge();
    }

}