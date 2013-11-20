package com.datayes.invest.pms.service.marketdata.impl;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.service.marketdata.impl.FutureMarketDataListener;
import com.datayes.invest.pms.service.marketdata.impl.MarketDataCache;
import com.datayes.invest.pms.service.marketdata.impl.data.Converter;
import com.datayes.invest.pms.service.marketdata.impl.data.Stock;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class StockMarketDataListener extends JedisPubSub  {
    private final MarketDataCache marketDataCache;

    private static Schema<Stock> stockSchema = RuntimeSchema.getSchema(Stock.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureMarketDataListener.class);

    public StockMarketDataListener(MarketDataCache marketDataCache) {
        this.marketDataCache = marketDataCache;
    }

    @Override
    public void onMessage(String channel, String message) {
        byte[] buff = Base64.decodeBase64(message);
        Stock stock = new Stock();
        ProtobufIOUtil.mergeFrom(buff, stock, stockSchema);

        MarketData md = Converter.toMarketData(stock);
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
