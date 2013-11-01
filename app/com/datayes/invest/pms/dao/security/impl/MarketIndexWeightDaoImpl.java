package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.MarketIndexWeightDao;
import com.datayes.invest.pms.entity.security.MarketIndexWeight;

import javax.persistence.Query;
import java.util.List;

public class MarketIndexWeightDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndexWeight, Long> implements MarketIndexWeightDao {

    protected MarketIndexWeightDaoImpl() {
        super(MarketIndexWeight.class);
    }

    @Override
    public MarketIndexWeight findLatestByMarketIndexIdSecurityId(Long marketIndexId, Long
            securityId) {
        Query q = getEntityManager().createQuery("from MarketIndexWeight where " +
                "marketIndexId = :marketIndexId and securityId = :securityId " +
                "order by effectiveDate desc");
        q.setParameter("marketIndexId", marketIndexId);
        q.setParameter("securityId", securityId);
        q.setMaxResults(1);
        enableCache(q);
        List<MarketIndexWeight> list = (List<MarketIndexWeight>) q.getResultList();

        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
