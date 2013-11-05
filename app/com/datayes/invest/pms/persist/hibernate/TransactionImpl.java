package com.datayes.invest.pms.persist.hibernate;

import java.util.EnumMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.Transaction;

public class TransactionImpl implements Transaction {
    
    private EnumMap<PersistUnit, EntityManager> entityManagers = new EnumMap<>(PersistUnit.class);
    
    @Override
    public void commit() {
        try {
            for (PersistUnit unit : entityManagers.keySet()) {
                EntityManager em = entityManagers.get(unit);
                EntityTransaction etx = em.getTransaction();
                if (etx != null && etx.isActive()) {
                    etx.commit();
                }
                em.close();
            }
        } finally {
            PersistServiceImpl.removeTransaction();
        }
    }

    @Override
    public void rollback() {
        try {
            for (PersistUnit unit : entityManagers.keySet()) {
                EntityManager em = entityManagers.get(unit);
                EntityTransaction etx = em.getTransaction();
                if (etx != null && etx.isActive()) {
                    etx.rollback();
                }
                em.close();
            }
        } finally {
            PersistServiceImpl.removeTransaction();
        }
    }

    public EntityManager getEntityManager(PersistUnit unit) {
        EntityManager em = entityManagers.get(unit);
        if (em != null) {
            return em;
        }
        em = PersistServiceImpl.getEntityManagerFactory(unit).createEntityManager();
        entityManagers.put(unit, em);
        if (! unit.isReadOnly()) {
            em.getTransaction().begin();
        }
        return em;
    }
}
