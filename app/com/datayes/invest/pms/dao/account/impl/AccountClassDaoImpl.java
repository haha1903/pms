package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import com.datayes.invest.pms.dao.account.AccountClassDao;
import com.datayes.invest.pms.entity.account.AccountClass;

public class AccountClassDaoImpl extends GenericAccountMasterDaoImpl<AccountClass, String>
    implements AccountClassDao {

    protected AccountClassDaoImpl() {
        super(AccountClass.class);
    }

    @SuppressWarnings("unchecked")
    public List<AccountClass> findAll() {
        List<AccountClass> results = (List<AccountClass>) getEntityManager().createQuery(
            "from AccountClass").getResultList();
        return results;
    }
}
