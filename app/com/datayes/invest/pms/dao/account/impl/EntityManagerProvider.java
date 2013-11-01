package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.hibernate.TransactionImpl;

import javax.persistence.EntityManager;

public class EntityManagerProvider {

    protected final EntityManager getEntityManager() {
        TransactionImpl tx = (TransactionImpl) Persist.currentTransaction();
        EntityManager em = tx.getEntityManager();
        return em;
    }
}
