package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.dbtype.CashTransactionType;
import com.datayes.invest.pms.entity.account.CashTransaction;
import org.joda.time.LocalDate;

import java.util.List;

public interface CashTransactionDao extends AccountRelatedGenericDao<CashTransaction, Long> {

    List<CashTransaction> findByAccountIdAsOfDateTypeCode(Long accountId, LocalDate asOfDate, CashTransactionType typeCode);
}
