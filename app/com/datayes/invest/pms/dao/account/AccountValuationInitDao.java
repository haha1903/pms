package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.AccountValuationInit;

public interface AccountValuationInitDao extends GenericAccountMasterDao<AccountValuationInit, Long> {

    AccountValuationInit findByAccountId(Long accountId);

    void deleteByAccountId(Long accountId);
}
