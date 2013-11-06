package com.datayes.invest.pms.service.marketdata.impl.data;

import java.sql.Timestamp;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.entity.security.FuturePriceVolume;
import com.datayes.invest.pms.entity.security.PriceVolume;
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

        return new MarketData(stock.getSecID(), ts, price, prevPrice);
    }

    public static MarketData toMarketData(Future future) {
        // In share price index future, it's volume must be bigger than 0
        if((null != future.getLastPrice() ||
                future.getLastPrice().compareTo(BigDecimalConstants.PRICE_EPSILON().toDouble()) > 0) &&
                (null != future.getVolume() || future.getVolume() > 0)) {
            
            BigDecimal price = new BigDecimal(java.math.BigDecimal.valueOf(future.getLastPrice()));
            BigDecimal prevPrice = new BigDecimal(java.math.BigDecimal.valueOf(future.getPreSettlePrice()));
            Timestamp ts = new Timestamp(future.getTimestamp());
            
            return new MarketData(future.getSecID(),ts, price, prevPrice);
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
        MarketData md = new MarketData(pv.getSecurityId(), ts, price, prevPrice);
        return md;
    }
    
    public static MarketData toMarketData(FuturePriceVolume pv) {
        // TODO the timestamp for future price volume
        Timestamp ts = new Timestamp(pv.getTradeDate().toDate().getTime());
        BigDecimal price = pv.getPriceSettle();
        BigDecimal prevPrice = pv.getPricePreviousClose();
        MarketData md = new MarketData(pv.getSecurityId(), ts, price, prevPrice);
        return md;
    }
}
