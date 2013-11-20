package com.datayes.invest.pms.service.marketdata.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.service.marketdata.impl.data.Converter;
import com.datayes.invest.pms.service.marketdata.impl.data.Future;
import com.google.gson.Gson;

public class FutureMarketDataListener extends JedisPubSub {

    private final Gson gson;
    private final MarketDataCache marketDataCache;

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureMarketDataListener.class);


    public FutureMarketDataListener(Gson gson, MarketDataCache marketDataCache) {
        this.gson = gson;
        this.marketDataCache = marketDataCache;
    }

    @Override
    public void onMessage(String channel, String message) {
        Future future = gson.fromJson(message, Future.class);
        MarketData md = Converter.toMarketData(future);
        if (md != null) {
            marketDataCache.update(md);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOGGER.debug("PMessage {} = {} = {}", pattern, channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("Subscribe to channel {} = {}", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info("Unsubsribe channel {} = {}" + channel + " = " + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        LOGGER.debug("PUnsubscribe " + pattern + "=" + subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.debug("PSubscribe " + pattern + "=" + subscribedChannels);
    }
}