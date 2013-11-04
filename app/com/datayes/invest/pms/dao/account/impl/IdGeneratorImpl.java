package com.datayes.invest.pms.dao.account.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.IdGenerator;

public class IdGeneratorImpl extends EntityManagerProvider implements IdGenerator {
    
    private final Object nextPositionIdLock = new Object();
    
    private AtomicLong nextPositionId = null;
    
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
    
    private long findLargestPositionId() {
        TypedQuery<Long> q = getEntityManager().createQuery("select max(id) from Position", Long.class);
        List<Long> list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return (long) list.get(0);
    }
}
