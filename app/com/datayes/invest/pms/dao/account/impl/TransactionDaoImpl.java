package com.datayes.invest.pms.dao.account.impl;

import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.datayes.invest.pms.dao.account.TransactionDao;
import com.datayes.invest.pms.entity.account.Transaction;

public class TransactionDaoImpl extends GenericAccountMasterDaoImpl<Transaction, Long> implements TransactionDao {

    protected TransactionDaoImpl() {
        super(Transaction.class);
    }

    @Override
    public void deleteByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
                        "delete from Transaction where accountId = :accountId and sourceTransactionDate >= (:startDateTime)"
                        + " and sourceTransactionDate < (:endDateTime)");
        q.setParameter("accountId", accountId);
        q.setParameter("startDateTime", asOfDate.toLocalDateTime(LocalTime.parse("00:00:00")));
        q.setParameter("endDateTime", asOfDate.plusDays(1).toLocalDateTime(LocalTime.parse("00:00:00")));
        q.executeUpdate();
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
                "delete from Transaction where accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }
}
