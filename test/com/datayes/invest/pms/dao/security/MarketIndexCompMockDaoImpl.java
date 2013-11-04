package com.datayes.invest.pms.dao.security;


import com.datayes.invest.pms.dao.security.impl.GenericSecurityMasterDaoImpl;
import com.datayes.invest.pms.entity.security.MarketIndexComp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketIndexCompMockDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndexComp, Long>
        implements MarketIndexCompDao {
    private Map<Long, MarketIndexComp> mockData = new HashMap<>();

    protected MarketIndexCompMockDaoImpl() {
        super(MarketIndexComp.class);
    }

    @Override
    public List<MarketIndexComp> findCurrentByMarketIndexId(Long marketIndexId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
