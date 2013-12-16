package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.CashTransactionDao;
import com.datayes.invest.pms.dbtype.CashTransactionType;
import com.datayes.invest.pms.entity.account.CashTransaction;
import org.joda.time.LocalDate;

import javax.persistence.Query;
import java.util.List;

public class CashTransactionDaoImpl extends AccountRelatedDaoImpl<CashTransaction, Long>
    implements CashTransactionDao {

    protected CashTransactionDaoImpl() {
        super(CashTransaction.class);
    }

    public List<CashTransaction> findByAccountIdAsOfDateTypeCode(Long accountId, LocalDate asOfDate, CashTransactionType typeCode) {
        Query q = getEntityManager().createQuery(
                "from CashTransaction where accountId = :accountId and asOfDate = :asOfDate and typeCode = :typeCode");

        q.setParameter("accountId", accountId);
        q.setParameter("asOfDate", asOfDate);
        q.setParameter("typeCode", typeCode);

        List<CashTransaction> list = (List<CashTransaction>) q.getResultList();
        return list;
    }
}
