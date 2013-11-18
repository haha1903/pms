package com.datayes.invest.pms.service.marketdata.impl;


import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.service.marketdata.impl.data.Converter;
import com.datayes.invest.pms.service.marketdata.impl.data.Stock;
import com.datayes.invest.pms.web.model.gson.BigDecimalDeserializer;
import com.datayes.invest.pms.web.model.gson.BigDecimalSerializer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scala.math.BigDecimal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class StockCacheUpdateTask implements Runnable {
    private final JedisPool pool;
    private final StockMarketDataListener listener;

    private final static Config config = Config.INSTANCE;
    private final static String CHANNEL = config.getString("redis.stock_channel");

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureCacheUpdateTask.class);

    public StockCacheUpdateTask(JedisPool pool, MarketDataCache marketDataCache) {
        this.pool = pool;
        listener = new StockMarketDataListener(marketDataCache);
    }

    @Override
    public void run() {
        LOGGER.info("Future Cache Scheduler begins");

        try {
            Jedis jedis = pool.getResource();
            jedis.subscribe(listener, CHANNEL);
        } catch (Throwable th){
            LOGGER.error(th.getMessage(), th);
        }
    }
}
