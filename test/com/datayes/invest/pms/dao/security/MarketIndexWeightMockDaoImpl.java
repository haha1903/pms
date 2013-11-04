package com.datayes.invest.pms.dao.security;


import com.datayes.invest.pms.dao.security.impl.GenericSecurityMasterDaoImpl;
import com.datayes.invest.pms.entity.security.MarketIndexWeight;

import java.util.HashMap;
import java.util.Map;

public class MarketIndexWeightMockDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndexWeight, Long>
        implements MarketIndexWeightDao {
    private Map<Long, MarketIndexWeight> mockData = new HashMap<>();

    protected MarketIndexWeightMockDaoImpl() {
        super(MarketIndexWeight.class);
    }


    @Override
    public MarketIndexWeight findLatestByMarketIndexIdSecurityId(Long marketIndexId, Long securityId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
