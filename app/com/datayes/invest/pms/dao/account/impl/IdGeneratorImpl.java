package com.datayes.invest.pms.dao.account.impl;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.PositionDao;

public class IdGeneratorImpl implements IdGenerator {

    @Inject
    private PositionDao positionDao;
    
    private final Object nextPositionIdLock = new Object();
    
    private AtomicLong nextPositionId = null;
    
    public Long getNextPositionId() {
        if (nextPositionId == null) {
            synchronized (nextPositionIdLock) {
                if (nextPositionId == null) {
                    long maxId = positionDao.findLargestPositionId();
                    nextPositionId = new AtomicLong(maxId + 1);
                }
            }
        }
        long id = nextPositionId.getAndIncrement();
        return id;
    }
}
