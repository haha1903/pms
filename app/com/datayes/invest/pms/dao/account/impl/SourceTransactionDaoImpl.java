package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.datayes.invest.pms.dao.account.SourceTransactionDao;
import com.datayes.invest.pms.entity.account.SourceTransaction;

public class SourceTransactionDaoImpl extends GenericAccountMasterDaoImpl<SourceTransaction, Long> implements
                SourceTransactionDao {

    protected SourceTransactionDaoImpl() {
        super(SourceTransaction.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourceTransaction> findByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
            "from SourceTransaction where accountId = :accountId and executionDate >= :startDateTime"
            + " and executionDate < :endDateTime");
        q.setParameter("accountId", accountId);
        q.setParameter("startDateTime", asOfDate.toLocalDateTime(LocalTime.parse("00:00:00")));
        q.setParameter("endDateTime", asOfDate.plusDays(1).toLocalDateTime(LocalTime.parse("00:00:00")));
        List<SourceTransaction> list = (List<SourceTransaction>) q.getResultList();
        return list;
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
                "delete from SourceTransaction where accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }
}
