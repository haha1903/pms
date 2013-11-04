package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.Account;

public interface AccountDao extends GenericAccountMasterDao<Account, Long> {
    
    public List<Account> findEffectiveAccounts(LocalDate asOfDate);
}
