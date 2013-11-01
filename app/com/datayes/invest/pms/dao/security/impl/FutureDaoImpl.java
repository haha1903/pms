package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import javax.persistence.Query;

import com.datayes.invest.pms.dao.security.FutureDao;
import com.datayes.invest.pms.entity.security.Future;

public class FutureDaoImpl extends GenericSecurityMasterDaoImpl<Future, Long> implements FutureDao {

    protected FutureDaoImpl() {
        super(Future.class);
    }

    @SuppressWarnings("unchecked")
    public Future findByTickerSymbol(String tickerSymbol) {
        Query q = getEntityManager().createQuery(
                "from Future where tickerSymbol = :tickerSymbol");
        q.setParameter("tickerSymbol", tickerSymbol);
        List<Future> list = (List<Future>) q.getResultList();

        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
