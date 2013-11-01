package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.AccountRelatedGenericDao;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

public class AccountRelatedDaoImpl<T, K extends Serializable> extends GenericAccountMasterDaoImpl<T, K>
    implements AccountRelatedGenericDao<T, K> {

    protected AccountRelatedDaoImpl(Class<T> clazz) {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    public List<T> findWithPagination(int pageSize, int page) {
        return (List<T>) getEntityManager().createQuery("from " + classOfEntity.getName())
            .setFirstResult(pageSize * page).setMaxResults(pageSize).getResultList();
    }

    public long findCount() {
        Integer num = (Integer) getEntityManager().createQuery(
            "select count(*) from " + classOfEntity.getName()).getResultList().get(0);
        return num.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
            "from " + classOfEntity.getName() + " where accountId = :accountId");
        q.setParameter("accountId", accountId);
        return (List<T>) q.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<T> findWithPaginationByAccountId(Long accountId, int pageSize, int page) {
        return (List<T>) getEntityManager().createQuery(
            "from " + classOfEntity.getName() + " where accountId = " + accountId)
            .setFirstResult(pageSize * page).setMaxResults(pageSize).getResultList();
    }

    public int findCountByAccountId(Long accountId) {
        Integer num = (Integer) getEntityManager().createQuery(
            "select count(*) from " + classOfEntity.getName() + " where accountId = " + accountId).
            getResultList().get(0);
        return num.intValue();
    }
}
