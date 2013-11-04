package com.datayes.invest.pms.service.marketdata.impl.cache;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.service.marketdata.impl.data.Head;
import com.datayes.invest.pms.service.marketdata.impl.data.MessageConverter;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MarketDataCache {
    private Map<Long, MarketData> realTimeCache = new ConcurrentHashMap<>();
    private Map<Long, LocalDateTime> cacheMissingSId = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataCache.class);


    public Map<Long, MarketData> getRealTimeCache() {
        Map<Long, MarketData> copyCache = new HashMap<>();

        Iterator iterator = realTimeCache.entrySet().iterator();
        while( iterator.hasNext() ) {
            Map.Entry<Long, MarketData> entry = (Map.Entry<Long, MarketData>) iterator.next();
            MarketData copyMarketData = new MarketData(entry.getValue());

            copyCache.put(entry.getKey(), copyMarketData);
        }

        return copyCache;
    }

    public MarketData findRealTimeMarketData(Long securityId) {
        MarketData marketData = realTimeCache.get(securityId);
        if( null == marketData ) {
            cacheMissingSId.put(securityId, LocalDateTime.now());
        }

        return marketData;
    }

    public MarketData update(MarketData marketData) {
        realTimeCache.put(marketData.getSecurityId(), marketData);
        return marketData;
    }

    public MarketData update(Head realTimeData) {
        MarketData marketData = MessageConverter.convertToMarketData(realTimeData);
        if( marketData != null ) {
            realTimeCache.put(marketData.getSecurityId(), marketData);
        }
        return marketData;
    }

    public Map<Long, LocalDateTime> getCacheMissingSecurityId() {
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
