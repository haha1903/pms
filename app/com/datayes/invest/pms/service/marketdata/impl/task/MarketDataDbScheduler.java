package com.datayes.invest.pms.service.marketdata.impl.task;


import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.service.marketdata.impl.cache.MarketDataCache;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class MarketDataDbScheduler extends TimerTask {

    private MarketDataCache marketDataCache = null;

    @Inject
    private MarketDataDao marketDataDao = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataDbScheduler.class);


    public MarketDataDbScheduler(MarketDataCache marketDataCache) {
        this.marketDataCache = marketDataCache;
    }

    private void saveMarketData(MarketData marketData) {
        MarketData oldMarketData = marketDataDao.findBySecurityId(marketData.getSecurityId());

        if( null == oldMarketData ) {
            marketDataDao.save(marketData);
        }
        else {
            marketDataDao.update(marketData);
        }
    }

    @Override
    public void run() {
        LOGGER.info("DB Scheduler begins");

        Transaction tx = Persist.beginTransaction();
        try {
            Map<Long, MarketData> realTimeCache = marketDataCache.getRealTimeCache();

            // Loop on every data in cache for writing into DB
            Iterator iterator = realTimeCache.entrySet().iterator();
            while( iterator.hasNext() ) {
                Map.Entry<Long, MarketData> entry = (Map.Entry<Long, MarketData>)iterator.next();
                saveMarketData(entry.getValue());
            }

            tx.commit();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            tx.rollback();
        }
    }
}
