package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.SecurityTransaction;

public interface SecurityTransactionDao extends AccountRelatedGenericDao<SecurityTransaction, Long> {

	List<SecurityTransaction> findRepoTransactionWithInterests(Long accountId, LocalDate asOfDate);

	List<SecurityTransaction> findExpiredRepoTransaction(Long accountId, LocalDate asOfDate);

	List<SecurityTransaction> findRepoTransactionOnReturnDate(Long accountId, LocalDate asOfDate);
}
