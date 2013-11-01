package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.AccountYieldDao;
import com.datayes.invest.pms.entity.account.AccountYield;

public class AccountYieldDaoImpl extends GenericAccountMasterDaoImpl<AccountYield, Long> implements
                AccountYieldDao {

    protected AccountYieldDaoImpl() {
        super(AccountYield.class);
    }
}
