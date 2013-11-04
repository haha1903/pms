package com.datayes.invest.pms.dao.account.impl;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.SecurityTransactionDao;
import com.datayes.invest.pms.entity.account.SecurityTransaction;
import com.datayes.invest.pms.dbtype.AssetClass;

public class SecurityTransactionDaoImpl extends AccountRelatedDaoImpl<SecurityTransaction, Long> implements
		SecurityTransactionDao {

	protected SecurityTransactionDaoImpl() {
		super(SecurityTransaction.class);
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

    private List<SecurityTransaction> findRepoTransaction(Long accountId, LocalDate asOfDate) {
        String query = "from " + classOfEntity.getName()
                + " where assetClassId=:assetClassId and accountId=:accountId";
        return enableCache(getEntityManager().createQuery(query, SecurityTransaction.class))
                .setParameter("assetClassId", AssetClass.REPO.getDbValue()).setParameter("accountId", accountId)
                .getResultList();
    }

}
