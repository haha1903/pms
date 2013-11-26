package com.datayes.invest.pms.dao.account.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;
import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.IdGenerator;

@Singleton
public class IdGeneratorImpl extends EntityManagerProvider implements IdGenerator {
    
    private final Object nextPositionIdLock = new Object();
    
    private final Object nextPositionYieldIdLock = new Object();
    
    private AtomicLong nextPositionId = null;
    
    private AtomicLong nextPositionYieldId = null;
    
    public Long getNextPositionId() {
        if (nextPositionId == null) {
            synchronized (nextPositionIdLock) {
                if (nextPositionId == null) {
                    long maxId = findLargestPositionId();
                    nextPositionId = new AtomicLong(maxId + 1);
                }
            }
        }
        long id = nextPositionId.getAndIncrement();
        return id;
    }
    
    @Override
    public Long getNextPositionYieldId() {
        if (nextPositionYieldId == null) {
            synchronized (nextPositionYieldIdLock) {
                if (nextPositionYieldId == null) {
                    long maxId = findLargestPositionYieldId();
                    nextPositionYieldId = new AtomicLong(maxId + 1);
                }
            }
        }
        long id = nextPositionId.getAndIncrement();
        return id;
    }
    
    private long findLargestPositionYieldId() {
        TypedQuery<Long> q = getEntityManager().createQuery("select max(id) from PositionYield", Long.class);
        List<Long> list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return (long) list.get(0);
    }

    private long findLargestPositionId() {
        TypedQuery<Long> q = getEntityManager().createQuery("select max(id) from Position", Long.class);
        List<Long> list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return (long) list.get(0);
    }
}
