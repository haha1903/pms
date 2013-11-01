package com.datayes.invest.pms.entity.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;


@SuppressWarnings("serial")
@Entity
@Table(name = "MARKETDATA_SNAPSHOT")
@Proxy(lazy = false)
public class MarketDataSnapshot extends EntityBase implements Serializable {

    public static final int EQUITY = 1;
    public static final int FUTURE = 2;

    private Long securityId;

    private LocalDateTime timestamp;

    private String ticker;

    private BigDecimal totalTradeValue;

    private BigDecimal totalTradeQuantity;

    private BigDecimal tradePrice;
    
    private BigDecimal previousClosePrice;

    private String exchangeCode;

    private LocalDateTime lastUpdate;

    @Transient
    private int dataType;

    private MarketDataSnapshot() {

    }

    public MarketDataSnapshot(Long securityId,
                              LocalDateTime timestamp,
                              String ticker,
                              BigDecimal totalTradeValue,
                              BigDecimal totalTradeQuantity,
                              BigDecimal tradePrice,
                              BigDecimal previousClosePrice,
                              String exchangeCode) {
        this.securityId = securityId;
        this.timestamp = timestamp;
        this.ticker = ticker;
        this.totalTradeValue = totalTradeValue;
        this.totalTradeQuantity = totalTradeQuantity;
        this.tradePrice = tradePrice;
        this.previousClosePrice = previousClosePrice;
        this.exchangeCode = exchangeCode;
        this.dataType = EQUITY;
    }

    @Id
    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "TIMESTAMP")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "")
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    @Column(name = "TOTAL_TRADE_VALUE")
    public BigDecimal getTotalTradeValue() {
        return totalTradeValue;
    }

    public void setTotalTradeValue(BigDecimal totalTradeValue) {
        this.totalTradeValue = totalTradeValue;
    }

    @Column(name = "TOTAL_TRADE_QUANTITY")
    public BigDecimal getTotalTradeQuantity() {
        return totalTradeQuantity;
    }

    public void setTotalTradeQuantity(BigDecimal totalTradeQuantity) {
        this.totalTradeQuantity = totalTradeQuantity;
    }

    @Column(name = "TRADE_PRICE")
    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(BigDecimal tradePrice) {
        this.tradePrice = tradePrice;
    }
    
//    @Column(name = "PREVIOUS_CLOSE_PRICE")
//    public BigDecimal getPreviousClosePrice() {
//        return previousClosePrice;
//    }
//
//    public void setPreviousClosePrice(BigDecimal previousClosePrice) {
//        this.previousClosePrice = previousClosePrice;
//    }

    @Column(name = "EXCHANGE_CD")
    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    @Column(name = "LAST_UPDATE")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Transient
    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}

