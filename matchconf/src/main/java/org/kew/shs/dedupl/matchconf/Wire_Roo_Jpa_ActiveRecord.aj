// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf;

import java.util.List;
import org.kew.shs.dedupl.matchconf.Wire;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Wire_Roo_Jpa_ActiveRecord {
    
    public static long Wire.countWires() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Wire o", Long.class).getSingleResult();
    }
    
    public static List<Wire> Wire.findAllWires() {
        return entityManager().createQuery("SELECT o FROM Wire o", Wire.class).getResultList();
    }
    
    public static Wire Wire.findWire(Long id) {
        if (id == null) return null;
        return entityManager().find(Wire.class, id);
    }
    
    public static List<Wire> Wire.findWireEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Wire o", Wire.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public Wire Wire.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Wire merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
