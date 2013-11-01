package com.datayes.invest.pms.dao.security.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.security.GenericSecurityMasterDao;

public class GenericSecurityMasterDaoImpl<T, K extends Serializable> extends EntityManagerProvider
        implements GenericSecurityMasterDao<T, K> {

    protected final Class<T> classOfEntity;

    protected GenericSecurityMasterDaoImpl(Class<T> clazz) {
        this.classOfEntity = clazz;
    }

    public T findById(K id) {
        return (T) getEntityManager().find(classOfEntity, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findWithPagination(int pageSize, int page) {
        return getEntityManager().createQuery("from " + classOfEntity.getName())
            .setFirstResult(pageSize * page).setMaxResults(pageSize).getResultList();
    }

    public long findCount() {
        Long num = (Long) getEntityManager().createQuery(
            "select count(*) from " + classOfEntity.getName()).getResultList().get(0);
        return num.longValue();
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
