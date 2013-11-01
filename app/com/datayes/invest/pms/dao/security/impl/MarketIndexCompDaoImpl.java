package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.MarketIndexCompDao;
import com.datayes.invest.pms.entity.security.MarketIndexComp;

import javax.persistence.Query;
import java.util.List;

public class MarketIndexCompDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndexComp, Long> implements MarketIndexCompDao {

    protected MarketIndexCompDaoImpl() {
        super(MarketIndexComp.class);
    }

    @Override
    public List<MarketIndexComp> findCurrentByMarketIndexId(Long marketIndexId) {
        Query q = getEntityManager().createQuery(
            "from MarketIndexComp where endDate = '0000-00-00' and marketIndexId = :marketIndexId");
        q.setParameter("marketIndexId", marketIndexId);
        enableCache(q);
        List<MarketIndexComp> list = (List<MarketIndexComp>) q.getResultList();
        return list;
    }
}
