package com.datayes.invest.pms.service.marketdata;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

public class MarketData {

    private final Long securityId;
    
    private final LocalDate asOfDate;
    
    private final BigDecimal price;
    
    private final BigDecimal previousPrice;

    public MarketData(Long securityId, LocalDate asOfDate, BigDecimal price, BigDecimal previousPrice) {
        this.securityId = securityId;
        this.asOfDate = asOfDate;
        this.price = price;
        this.previousPrice = previousPrice;
    }

    public Long getSecurityId() {
        return securityId;
    }
    
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }
}
