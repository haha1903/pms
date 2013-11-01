package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.AccountValuationTypeDao;
import com.datayes.invest.pms.entity.account.AccountValuationType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * System: Ubuntu
 * User: baoan @datayes
 * Date: 8/27/13
 * Time: 3:22 PM
 */
public class AccountValuationTypeDaoImpl extends GenericAccountMasterDaoImpl<AccountValuationType, Long>
    implements AccountValuationTypeDao {

    protected AccountValuationTypeDaoImpl(){
        super(AccountValuationType.class);
    }

    @SuppressWarnings("unchecked")
    public List<AccountValuationType> findAll(){
        List<AccountValuationType> results = (List<AccountValuationType>) getEntityManager().createQuery(
                "from AccountValuationType").getResultList();
        return results;
    }
}
