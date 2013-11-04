package com.datayes.invest.pms.dao.account.cacheimpl;

import java.io.Serializable;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.GenericAccountMasterDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Cache;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace;

abstract class DaoCacheImpl<T, K extends Serializable> implements GenericAccountMasterDao<T, K> {
    
    @Inject
    protected CacheWorkspace cacheWorkspace;
    
    private final Class<T> classOfEntity;
    
    protected DaoCacheImpl(Class<T> clazz) {
        this.classOfEntity = clazz;
    }
    
    protected Cache<T> getCache() {
        return cacheWorkspace.get(classOfEntity);
    }

    public void delete(T entity) {
        throw new UnsupportedOperationException();
    }

    public void detach(T entity) {
        throw new UnsupportedOperationException();
    }
}
