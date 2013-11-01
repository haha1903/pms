package com.datayes.invest.pms.persist.hibernate;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Stack;

import javax.persistence.EntityManagerFactory;

import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.PersistException;
import com.datayes.invest.pms.persist.PersistService;
import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.Transaction;

public class PersistServiceImpl implements PersistService {

    private static final ThreadLocal<Stack<TransactionImpl>> transactionsThreadLocal = new ThreadLocal<>();
    
    private static final EnumMap<PersistUnit, EntityManagerFactory> entityManagerFactories = new EnumMap<>(PersistUnit.class);

    @Override
    public void initialize() {
        Persist.setPersistService(this);
    }
    
    @Override
    public Transaction beginTransaction(PersistUnit persistUnit) {
        
        Stack<TransactionImpl> stack = getStack();
        
        // Check if a transaction for the persist unit already exists in current thread
        int size = stack.size();
        for (int i = 0; i < size; i++) {
            TransactionImpl tx = stack.get(i);
            if (tx.getPersistUnit() == persistUnit) {
                throw new PersistException("Failed to begin transaction. Transaction for " + persistUnit +
                    " already exists in current thread");
            }
        }
        
        TransactionImpl tx = new TransactionImpl(persistUnit);
        
        stack.push(tx);
        return tx;
    }

    @Override
    public Transaction currentTransaction() {
        Stack<TransactionImpl> stack = getStack();
        if (stack == null || stack.empty()) {
            return null;
        }
        TransactionImpl tx = stack.peek();
        return tx;
    }
    
    static void removeTransaction(TransactionImpl tx) {
        Stack<TransactionImpl> stack = getStack();
        Iterator<TransactionImpl> iter = stack.iterator();
        while (iter.hasNext()) {
            TransactionImpl t = iter.next();
            if (t == tx) {
                iter.remove();
            }
        }
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

    private static Stack<TransactionImpl> getStack() {
        Stack<TransactionImpl> stack = transactionsThreadLocal.get();
        if (stack == null) {
            stack = new Stack<TransactionImpl>();
            transactionsThreadLocal.set(stack);
        }
        return stack;
    }
}
