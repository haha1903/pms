package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.MarketIndexDao;
import com.datayes.invest.pms.entity.security.MarketIndex;

import java.util.List;

public class MarketIndexDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndex, Long> implements MarketIndexDao {

    protected MarketIndexDaoImpl() {
        super(MarketIndex.class);
    }

    public List<MarketIndex> findAll() {
        List<MarketIndex> list = (List<MarketIndex>) enableCache(getEntityManager().createQuery(
            "from MarketIndex")).getResultList();
        return list;
    }
}
