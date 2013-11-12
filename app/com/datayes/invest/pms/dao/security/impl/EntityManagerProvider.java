package com.datayes.invest.pms.dao.security.impl;

import javax.persistence.EntityManager;

import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.hibernate.TransactionImpl;

public class EntityManagerProvider {

    protected final EntityManager getEntityManager() {
        TransactionImpl tx = (TransactionImpl) Persist.currentTransaction();
        EntityManager em = tx.getEntityManager(PersistUnit.SECURITY_MASTER);
        return em;
    }
}
