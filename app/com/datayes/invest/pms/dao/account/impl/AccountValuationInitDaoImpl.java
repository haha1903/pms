package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.AccountValuationInitDao;
import com.datayes.invest.pms.entity.account.AccountValuationInit;

import javax.persistence.Query;
import java.util.List;


public class AccountValuationInitDaoImpl extends GenericAccountMasterDaoImpl<AccountValuationInit, Long>
    implements AccountValuationInitDao {

    protected AccountValuationInitDaoImpl(){
        super(AccountValuationInit.class);
    }

    public AccountValuationInit findByAccountId(Long accountId) {

        Query q = getEntityManager().createQuery(
                "from AccountValuationInit where accountId = :accountId");
        q.setParameter("accountId", accountId);
        List<AccountValuationInit> list = (List<AccountValuationInit>) q.getResultList();

        if(!list.isEmpty())
            return list.get(0);
        else
            return null;
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery("delete from AccountValuationInit where accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }
}
