package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.entity.account.Account;


public class AccountDaoImpl extends GenericAccountMasterDaoImpl<Account, Long> implements AccountDao {

    protected AccountDaoImpl() {
        super(Account.class);
    }

    @SuppressWarnings("unchecked")
    public List<Account> findEffectiveAccounts(LocalDate asOfDate) {
        Query q = getEntityManager().createQuery("from Account where openDate <= (:asOfDate)");
        q.setParameter("asOfDate", new LocalDateTime(asOfDate.toDateTimeAtStartOfDay()));
        enableCache(q);
        List<Account> list = (List<Account>) q.getResultList();
        return list;
    }
}
