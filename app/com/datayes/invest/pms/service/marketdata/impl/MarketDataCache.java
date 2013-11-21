package com.datayes.invest.pms.service.marketdata.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.invest.pms.entity.account.MarketData;


class MarketDataCache {
    private ConcurrentMap<Long, MarketData> realTimeCache = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, LocalDateTime> cacheMissingSId = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataCache.class);


    Map<Long, MarketData> getRealTimeCache() {
        Map<Long, MarketData> copyCache = new HashMap<>();

        Iterator<Map.Entry<Long, MarketData>> iterator = realTimeCache.entrySet().iterator();
        while( iterator.hasNext() ) {
            Map.Entry<Long, MarketData> entry = iterator.next();
            MarketData cloned = entry.getValue().clone();

            copyCache.put(entry.getKey(), cloned);
        }

        return copyCache;
    }

    MarketData findRealTimeMarketData(Long securityId) {
        MarketData marketData = realTimeCache.get(securityId);
        if( null == marketData ) {
            cacheMissingSId.put(securityId, LocalDateTime.now());
        }

        return marketData;
    }

    MarketData update(MarketData marketData) {
        if (marketData.getSecurityId() == null) {
            LOGGER.warn("market data has no security id: {}", marketData.toString());
        } else {
            realTimeCache.put(marketData.getSecurityId(), marketData);
        }
        return marketData;
    }

    Map<Long, LocalDateTime> getCacheMissingSecurityId() {
        Map<Long, LocalDateTime> copy = new HashMap<>();

        Iterator iterator = cacheMissingSId.entrySet().iterator();
        while( iterator.hasNext() ) {
            Map.Entry<Long, LocalDateTime> entry = (Map.Entry<Long, LocalDateTime>)iterator.next();
            LocalDateTime copyTime = new LocalDateTime(entry.getValue());

            copy.put(entry.getKey(), copyTime);
        }

        return copy;
    }
}
