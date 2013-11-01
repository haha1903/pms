package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.EquityDao;
import com.datayes.invest.pms.entity.security.Equity;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

public class EquityDaoImpl extends GenericSecurityMasterDaoImpl<Equity, Long> implements EquityDao {

    protected EquityDaoImpl() {
        super(Equity.class);
    }

    public List<Equity> findByTickerSymbol(String tickerSymbol) {
        Query q = getEntityManager().createQuery(
                "from Equity where tickerSymbol = :tickerSymbol");
        q.setParameter("tickerSymbol", tickerSymbol);
        enableCache(q);
        List<Equity> list = (List<Equity>) q.getResultList();

        return list;
    }

    @Override
    public List<Equity> findListed() {
        TypedQuery<Equity> q = getEntityManager().createQuery("from Equity where listingStatus = :listingStatus", Equity.class);
        q.setParameter("listingStatus", 2);
        List<Equity> list = q.getResultList();
        return list;
    }
}
