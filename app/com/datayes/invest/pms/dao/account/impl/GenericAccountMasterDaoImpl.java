package com.datayes.invest.pms.dao.account.impl;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.GenericAccountMasterDao;

class GenericAccountMasterDaoImpl<T, K extends Serializable> extends EntityManagerProvider
        implements GenericAccountMasterDao<T, K> {

    protected final Class<T> classOfEntity;

    protected GenericAccountMasterDaoImpl(Class<T> clazz) {
        this.classOfEntity = clazz;
    }

    public T findById(K id) {
        return getEntityManager().find(classOfEntity, id);
    }

    public void delete(T entity) {
        getEntityManager().remove(entity);
    }

    public void save(T entity) {
        getEntityManager().persist(entity);
    }

    public void update(T entity) {
        getEntityManager().merge(entity);
    }

    public void detach(T entity) {
        getEntityManager().detach(entity);
    }

    protected Query enableCache(Query query) {
    	query.setHint("org.hibernate.cacheable", true);
    	return query;
    }    

    protected TypedQuery<T> enableCache(TypedQuery<T> query) {
    	query.setHint("org.hibernate.cacheable", true);
    	return query;
    }    
}
