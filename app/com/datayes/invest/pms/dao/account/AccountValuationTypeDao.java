package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.AccountValuationType;

import java.util.List;

public interface AccountValuationTypeDao extends GenericAccountMasterDao<AccountValuationType, Long> {
    
    public List<AccountValuationType> findAll();
}
