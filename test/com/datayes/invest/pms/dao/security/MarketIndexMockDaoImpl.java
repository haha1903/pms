package com.datayes.invest.pms.dao.security;


import com.datayes.invest.pms.dao.security.impl.GenericSecurityMasterDaoImpl;
import com.datayes.invest.pms.entity.security.MarketIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketIndexMockDaoImpl extends GenericSecurityMasterDaoImpl<MarketIndex, Long> implements MarketIndexDao {
    private Map<Long, MarketIndex> mockData = new HashMap<>();

    protected MarketIndexMockDaoImpl() {
        super(MarketIndex.class);
    }

    @Override
    public List<MarketIndex> findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
