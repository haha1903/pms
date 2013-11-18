package com.datayes.invest.pms.service.marketdata.impl.data;

import java.sql.Timestamp;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.entity.security.FuturePriceVolume;
import com.datayes.invest.pms.entity.security.PriceVolume;
import com.datayes.invest.pms.service.marketdata.impl.Source;
import com.datayes.invest.pms.util.BigDecimalConstants;


public class Converter {
    
    private Converter() {
    }

    public static MarketData toMarketData(Stock stock) {
        Double dPrice;
        if(stock.getLastPrice().compareTo(BigDecimalConstants.PRICE_EPSILON().toDouble()) < 0) {
            dPrice = stock.getPrevClosePrice();
        }
        else {
            dPrice = stock.getLastPrice();
        }
        
        BigDecimal price = new BigDecimal(java.math.BigDecimal.valueOf(dPrice));
        BigDecimal prevPrice = new BigDecimal(java.math.BigDecimal.valueOf(stock.getPrevClosePrice()));
        Timestamp ts = new Timestamp(stock.getTimestamp());
        LocalDate asOfDate = new LocalDate(stock.getTimestamp());
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return new MarketData(stock.getSecID(), asOfDate, ts, price, prevPrice, now, Source.REDIS_STOCK.toString());
    }

    public static MarketData toMarketData(Future future) {
        // In share price index future, it's volume must be bigger than 0
        if((null != future.getLastPrice() ||
                future.getLastPrice().compareTo(BigDecimalConstants.PRICE_EPSILON().toDouble()) > 0) &&
                (null != future.getVolume() || future.getVolume() > 0)) {
            
            BigDecimal price = new BigDecimal(java.math.BigDecimal.valueOf(future.getLastPrice()));
            BigDecimal prevPrice = new BigDecimal(java.math.BigDecimal.valueOf(future.getPreSettlePrice()));
            Timestamp ts = new Timestamp(future.getTimestamp());
            LocalDate asOfDate = new LocalDate(ts.getTime());
            Timestamp now = new Timestamp(System.currentTimeMillis());

            return new MarketData(future.getSecID(), asOfDate, ts, price, prevPrice, now, Source.REDIS_FUTURE.toString());
        }
        else {
            return null;
        }
    }

    public static MarketData toMarketData(PriceVolume pv) {
        // TODO the timestamp for price volume
        Timestamp ts = new Timestamp(pv.getTradeDate().toDate().getTime());
        BigDecimal price = new BigDecimal(java.math.BigDecimal.valueOf(pv.getPriceClose()));
        BigDecimal prevPrice = new BigDecimal(java.math.BigDecimal.valueOf(pv.getPricePreviousClose()));
        MarketData md = new MarketData(pv.getSecurityId(), pv.getTradeDate(), ts, price, prevPrice, null,
            Source.PRICE_VOLUME.toString());
        return md;
    }
    
    public static MarketData toMarketData(FuturePriceVolume pv) {
        // TODO the timestamp for future price volume
        Timestamp ts = new Timestamp(pv.getTradeDate().toDate().getTime());
        BigDecimal price = pv.getPriceSettle();
        BigDecimal prevPrice = pv.getPricePreviousClose();
        MarketData md = new MarketData(pv.getSecurityId(), pv.getTradeDate(), ts, price, prevPrice, null,
            Source.FUT_PRICEVOLUME.toString());
        return md;
    }
}
