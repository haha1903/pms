package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.entity.account.SecurityTransaction;
import org.joda.time.LocalDate;

import java.util.List;

public interface SecurityTransactionDao extends AccountRelatedGenericDao<SecurityTransaction, Long> {

    List<SecurityTransaction> findByAccountIdListBetweenDates(List<Long> accountIds, LocalDate startDate, LocalDate endDate);

	List<SecurityTransaction> findRepoTransactionWithInterests(Long accountId, LocalDate asOfDate);

	List<SecurityTransaction> findExpiredRepoTransaction(Long accountId, LocalDate asOfDate);

	List<SecurityTransaction> findRepoTransactionOnReturnDate(Long accountId, LocalDate asOfDate);

    List<SecurityTransaction> findTransactionsExecDateTradeSide(List<Long> securityIds, LocalDate execDate, TradeSide tradeSide);
}
