package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.AccountValuationHistDao;
import com.datayes.invest.pms.entity.account.AccountValuationHist;

public class AccountValuationHistDaoImpl extends GenericAccountMasterDaoImpl<AccountValuationHist, AccountValuationHist.PK>
        implements AccountValuationHistDao {

    protected AccountValuationHistDaoImpl() {
        super(AccountValuationHist.class);
    }

    public List<AccountValuationHist> findByAccountIdTypeIdBeforeDate(
            Long accountId, Long typeId, LocalDate beforeDate) {
        Query q = getEntityManager().createQuery(
            "from AccountValuationHist where PK.accountId = :accountId and PK.typeId = :typeId " +
            "and PK.asOfDate <= :beforeDate order by PK.asOfDate asc");
        q.setParameter("accountId", accountId);
        q.setParameter("typeId", typeId);
        q.setParameter("beforeDate", beforeDate);
        enableCache(q);

        return (List<AccountValuationHist>) q.getResultList();
    }

    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
                "delete from AccountValuationHist where PK.accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }

    @Override
    public List<AccountValuationHist> findByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
                        "from AccountValuationHist where PK.accountId = :accountId and PK.asOfDate = :asOfDate");
        q.setParameter("accountId", accountId);
        q.setParameter("asOfDate", asOfDate);
        List<AccountValuationHist> list = q.getResultList();
        return list;
    }
}
