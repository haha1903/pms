package com.datayes.invest.pms.dao.account;

import java.util.List;

import com.datayes.invest.pms.entity.account.AccountClass;

public interface AccountClassDao extends GenericAccountMasterDao<AccountClass, String> {
    
    public List<AccountClass> findAll();
}
