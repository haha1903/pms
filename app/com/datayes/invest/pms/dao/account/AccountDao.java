package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.Account;

public interface AccountDao extends GenericAccountMasterDao<Account, Long> {

    /*
     * Find efffective accounts (by open date) and account status
     */
    public List<Account> findEffectiveAccounts(LocalDate asOfDate);

    /*
     * Find all accounts without considering open date or status. This should only be used when checking constraints
     */
    public List<Account> findAll();
}
