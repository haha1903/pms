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
    public List<T> findByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
            "from " + classOfEntity.getName() + " where accountId = :accountId");
        q.setParameter("accountId", accountId);
        return (List<T>) q.getResultList();
    }
}
