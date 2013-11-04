package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.AccountValuationHist;

public interface AccountValuationHistDao extends GenericAccountMasterDao<AccountValuationHist, AccountValuationHist.PK> {
    
    List<AccountValuationHist> findByAccountIdAsOfDate(Long accountId, LocalDate asOfDate);

    List<AccountValuationHist> findByAccountIdTypeIdBeforeDate(Long accountId, Long typeId, LocalDate beforeDate);

    void deleteByAccountId(Long accountId);
}
