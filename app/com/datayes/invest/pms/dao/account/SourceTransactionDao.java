package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.SourceTransaction;

public interface SourceTransactionDao extends GenericAccountMasterDao<SourceTransaction, Long>{

    List<SourceTransaction> findByAccountIdAsOfDate(Long accountId, LocalDate asOfDate);

    void deleteByAccountId(Long accountId);
}
