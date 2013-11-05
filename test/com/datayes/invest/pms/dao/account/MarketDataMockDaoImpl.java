package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.dao.account.impl.GenericAccountMasterDaoImpl;
import com.datayes.invest.pms.entity.account.MarketData;

import java.util.*;

public class MarketDataMockDaoImpl extends GenericAccountMasterDaoImpl<MarketData, Long>
        implements MarketDataDao{

    private Map<Long, MarketData> mockData = new HashMap<>();

    protected MarketDataMockDaoImpl() {
        super(MarketData.class);
    }

    @Override
    public MarketData findBySecurityId(Long securityId) {
        return mockData.get(securityId);
    }

    @Override
    public List<MarketData> findAll() {
        List<MarketData> list = new LinkedList<>();

        Iterator iterator = mockData.entrySet().iterator();
        while( iterator.hasNext() ) {
            Map.Entry<Long, MarketData> entry = (Map.Entry<Long, MarketData>)iterator.next();
            list.add(entry.getValue());
        }

        return list;
    }

    public void addMockData(MarketData marketData) {
        mockData.put(marketData.getSecurityId(), marketData);
    }
}
