// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.kew.shs.dedupl.matchconf.WiredTransformer;
import org.springframework.transaction.annotation.Transactional;

privileged aspect WiredTransformer_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager WiredTransformer.entityManager;
    
    public static final EntityManager WiredTransformer.entityManager() {
        EntityManager em = new WiredTransformer().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long WiredTransformer.countWiredTransformers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM WiredTransformer o", Long.class).getSingleResult();
    }
    
    public static List<WiredTransformer> WiredTransformer.findAllWiredTransformers() {
        return entityManager().createQuery("SELECT o FROM WiredTransformer o", WiredTransformer.class).getResultList();
    }
    
    public static WiredTransformer WiredTransformer.findWiredTransformer(Long id) {
        if (id == null) return null;
        return entityManager().find(WiredTransformer.class, id);
    }
    
    public static List<WiredTransformer> WiredTransformer.findWiredTransformerEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM WiredTransformer o", WiredTransformer.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void WiredTransformer.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void WiredTransformer.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            WiredTransformer attached = WiredTransformer.findWiredTransformer(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void WiredTransformer.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void WiredTransformer.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public WiredTransformer WiredTransformer.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        WiredTransformer merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}