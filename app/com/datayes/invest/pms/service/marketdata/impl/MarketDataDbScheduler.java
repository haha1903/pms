package com.datayes.invest.pms.service.marketdata.impl;


import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

class MarketDataDbScheduler extends TimerTask {

    private MarketDataCache marketDataCache = null;

    private MarketDataDao marketDataDao = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataDbScheduler.class);


    public MarketDataDbScheduler(MarketDataCache marketDataCache, MarketDataDao marketDataDao) {
        this.marketDataCache = marketDataCache;
        this.marketDataDao = marketDataDao;
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
        LOGGER.debug("Market data DB Scheduler started");

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
            LOGGER.info("Market data DB Scheduler finished");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            tx.rollback();
        }
    }
}
