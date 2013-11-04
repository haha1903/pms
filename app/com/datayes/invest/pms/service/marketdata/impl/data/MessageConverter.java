package com.datayes.invest.pms.service.marketdata.impl.data;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.util.BigDecimalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.math.BigDecimal;

import java.sql.Timestamp;


public class MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConverter.class);

    private static MarketData createByStock(Stock stock) {
        Double price;
        if(stock.getLastPrice().compareTo(BigDecimalConstants.PRICE_EPSILON().toDouble()) < 0) {
            price = stock.getPrevClosePrice();
        }
        else {
            price = stock.getLastPrice();
        }

        return new MarketData(
                stock.getSecID(),
                new Timestamp(stock.getTimestamp()),
                new BigDecimal(new java.math.BigDecimal(price)),
                new BigDecimal(new java.math.BigDecimal(stock.getPrevClosePrice())),
                MarketData.EQUITY);
    }

    private static MarketData createByFuture(Future future) {
        // In share price index future, it's volume must be bigger than 0
        if((null != future.getLastPrice() ||
                future.getLastPrice().compareTo(BigDecimalConstants.PRICE_EPSILON().toDouble()) > 0) &&
                (null != future.getVolume() || future.getVolume() > 0)) {
            return new MarketData(
                    future.getSecID(),
                    //new LocalDateTime(new Date(marketMetaData.getTimestamp.toLong*1000 + marketMetaData.getMillisecond)),
                    new Timestamp(future.getTimestamp()),
                    new BigDecimal(new java.math.BigDecimal(future.getLastPrice())),
                    new BigDecimal(new java.math.BigDecimal(future.getPreSettlePrice())),
                    MarketData.FUTURE);
        }
        else {
            return null;
        }
    }

    public static MarketData convertToMarketData(Head message) {
        if( Stock.class.isInstance(message) ) {
            return createByStock((Stock)message);
        }
        else if( Future.class.isInstance(message)) {
            return createByFuture((Future)message);
        }
        else {
            LOGGER.error("Message doesn't has any inheritance");
            return null;
        }
    }
}
