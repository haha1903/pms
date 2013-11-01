package com.datayes.invest.pms.persist.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.Transaction;

public class TransactionImpl implements Transaction {
    
    private final PersistUnit persistUnit;
    
    private EntityManager em;
    
    TransactionImpl(PersistUnit persistUnit) {
        this.persistUnit = persistUnit;
    }

    @Override
    public void commit() {
        if (em == null) {
            return;
        }
        try {
            EntityTransaction etx = em.getTransaction();
            if (etx != null && etx.isActive()) {
                etx.commit();
            }
            em.close();
        } finally {
            PersistServiceImpl.removeTransaction(this);
        }
    }

    @Override
    public void rollback() {
        if (em == null) {
            return;
        }
        try {
            EntityTransaction etx = em.getTransaction();
            if (etx != null && etx.isActive()) {
                etx.rollback();
            }
            em.close();
        } finally {
            PersistServiceImpl.removeTransaction(this);
        }
    }

    public EntityManager getEntityManager() {
        if (em != null) {
            return em;
        }
        em = PersistServiceImpl.getEntityManagerFactory(persistUnit).createEntityManager();
        if (! persistUnit.isReadOnly()) {
            em.getTransaction().begin();
        }
        return em;
    }
    
    PersistUnit getPersistUnit() {
        return persistUnit;
    }

}
