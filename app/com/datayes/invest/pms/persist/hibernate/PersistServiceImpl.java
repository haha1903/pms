package com.datayes.invest.pms.persist.hibernate;

import java.util.EnumMap;

import javax.persistence.EntityManagerFactory;

import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.PersistException;
import com.datayes.invest.pms.persist.PersistService;
import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.Transaction;

public class PersistServiceImpl implements PersistService {

    private static final ThreadLocal<TransactionImpl> transactionThreadLocal = new ThreadLocal<>();
    
    private static final EnumMap<PersistUnit, EntityManagerFactory> entityManagerFactories = new EnumMap<>(PersistUnit.class);

    @Override
    public void initialize() {
        Persist.setPersistService(this);
    }
    
    @Override
    public Transaction beginTransaction() {
        if (transactionThreadLocal.get() != null) {
            throw new PersistException("Transaction already exists in current thread. Cannot begin transaction.");
        }
        TransactionImpl tx = new TransactionImpl();
        transactionThreadLocal.set(tx);
        return tx;
    }

    @Override
    public Transaction currentTransaction() {
        Transaction tx = transactionThreadLocal.get();
        return tx;
    }
    
    static void removeTransaction() {
        if (transactionThreadLocal.get() == null) {
            throw new PersistException("No transaction exists in current thread. Cannot not remove transaction.");
        }
        transactionThreadLocal.remove();
    }
    
    static EntityManagerFactory getEntityManagerFactory(PersistUnit unit) {
        EntityManagerFactory emf = entityManagerFactories.get(unit);
        if (emf == null) {
            synchronized (entityManagerFactories) {
                if (emf == null) {
                    emf = HibernatePersistence.createEntityManagerFactory(unit);
                }
                entityManagerFactories.put(unit, emf);
            }
        }
        return emf;
    }
}
