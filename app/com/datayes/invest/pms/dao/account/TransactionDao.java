package com.datayes.invest.pms.dao.account;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.Transaction;


public interface TransactionDao extends GenericAccountMasterDao<Transaction, Long> {

    void deleteByAccountIdAsOfDate(Long accountId, LocalDate asOfDate);

    void deleteByAccountId(Long accountId);
}
