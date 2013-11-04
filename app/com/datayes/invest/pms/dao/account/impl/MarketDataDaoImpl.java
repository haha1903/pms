package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.entity.account.MarketData;

import javax.persistence.Query;
import java.util.List;


public class MarketDataDaoImpl extends GenericAccountMasterDaoImpl<MarketData, Long>
    implements MarketDataDao {

    protected MarketDataDaoImpl() {
        super(MarketData.class);
    }

    @Override
    public MarketData findBySecurityId(Long securityId) {
        Query q = getEntityManager().createQuery(
                "from MarketDataSnapshot where securityId = :securityId");
        q.setParameter("securityId", securityId);

        List<MarketData> list = (List<MarketData>) q.getResultList();

        if(!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<MarketData> findAll() {
        Query q = getEntityManager().createQuery(
                "from MarketDataSnapshot");

        List<MarketData> list = (List<MarketData>) q.getResultList();
        return list;
    }
}
