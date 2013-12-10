package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractIdGenerator extends EntityManagerProvider implements IdGenerator {

    protected final Object lock = new Object();

    protected AtomicLong nextId = null;

    public Long getNextId() {
        if (nextId == null) {
            synchronized (lock) {
                if (nextId == null) {
                    long maxId = findMaxId();
                    nextId = new AtomicLong(maxId + 1);
                }
            }
        }
        long id = nextId.getAndIncrement();
        return id;
    }

    protected abstract long findMaxId();
}
