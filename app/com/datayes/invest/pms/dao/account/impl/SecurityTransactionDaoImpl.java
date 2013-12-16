package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.SecurityTransactionDao;
import com.datayes.invest.pms.dbtype.AssetClass;
import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.entity.account.SecurityTransaction;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SecurityTransactionDaoImpl extends AccountRelatedDaoImpl<SecurityTransaction, Long> implements
		SecurityTransactionDao {

	protected SecurityTransactionDaoImpl() {
		super(SecurityTransaction.class);
	}

    @Override
    public List<SecurityTransaction> findByAccountIdListBetweenDates(List<Long> accountIds, LocalDate startDate, LocalDate endDate) {
        if (accountIds == null || accountIds.isEmpty()) {
            return Collections.emptyList();
        }
        if (startDate.isAfter(endDate)) {
            return Collections.emptyList();
        }

        TypedQuery<SecurityTransaction> q = getEntityManager().createQuery(
                "from SecurityTransaction where accountId in (:accountIds) and asOfDate >= (:startDate)"
                        + " and asOfDate < (:endDate)", SecurityTransaction.class);
        q.setParameter("accountIds", accountIds);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate.plusDays(1));
        List<SecurityTransaction> list = q.getResultList();

        return list;
    }


    public List<SecurityTransaction> findRepoTransactionWithInterests(Long accountId, LocalDate asOfDate) {
		List<SecurityTransaction> list = findRepoTransaction(accountId, asOfDate);
		List<SecurityTransaction> result = new LinkedList<SecurityTransaction>();
		for (SecurityTransaction transaction : list) {
			if (transaction.getExecutionDate() != null && transaction.getExecutionDate().isBefore(asOfDate)
					&& transaction.getReturnDate() != null && transaction.getReturnDate().isAfter(asOfDate)) {
				result.add(transaction);
			}
		}

		return result;
	}

	public List<SecurityTransaction> findExpiredRepoTransaction(Long accountId, LocalDate asOfDate) {
		List<SecurityTransaction> list = findRepoTransaction(accountId, asOfDate);
		List<SecurityTransaction> result = new LinkedList<SecurityTransaction>();
		for (SecurityTransaction transaction : list) {
			if (transaction.getSettlementDate() != null && transaction.getSettlementDate().isEqual(asOfDate)) {
				result.add(transaction);
			}
		}

		return result;
	}

	public List<SecurityTransaction> findRepoTransactionOnReturnDate(Long accountId, LocalDate asOfDate) {
		List<SecurityTransaction> list = findRepoTransaction(accountId, asOfDate);
		List<SecurityTransaction> result = new LinkedList<SecurityTransaction>();
		for (SecurityTransaction transaction : list) {
			if (transaction.getReturnDate() != null && transaction.getReturnDate().isEqual(asOfDate)) {
				result.add(transaction);
			}
		}

		return result;
	}

    public List<SecurityTransaction> findTransactionsExecDateTradeSide(Long accountId, List<Long> securityIds, LocalDate execDate, TradeSide tradeSide) {
        LocalDateTime startDateTime = new LocalDateTime(execDate.toDateTimeAtStartOfDay());
        LocalDateTime endDateTime = new LocalDateTime(execDate.plusDays(1).toDateTimeAtStartOfDay());
        Query q = getEntityManager().createQuery(" from " + classOfEntity.getName()
                + " where accountId = :accountId and securityId in :securitIds and executionDate >= :startDateTime and executionDate < :endDateTime and tradeSideCode = :tradeSideCode");

        q.setParameter("accountId", accountId);
        q.setParameter("securitIds", securityIds);
        q.setParameter("startDateTime", startDateTime);
        q.setParameter("endDateTime", endDateTime);
        q.setParameter("tradeSideCode", tradeSide.getDbValue());
        enableCache(q);

        return (List<SecurityTransaction>) q.getResultList();
    }

    private List<SecurityTransaction> findRepoTransaction(Long accountId, LocalDate asOfDate) {
        String query = "from " + classOfEntity.getName()
                + " where assetClassId=:assetClassId and accountId=:accountId";
        return enableCache(getEntityManager().createQuery(query, SecurityTransaction.class))
                .setParameter("assetClassId", AssetClass.REPO.getDbValue()).setParameter("accountId", accountId)
                .getResultList();
    }

}
