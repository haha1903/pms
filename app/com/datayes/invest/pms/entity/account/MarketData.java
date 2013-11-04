package com.datayes.invest.pms.entity.account;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;
import scala.math.BigDecimal;

import javax.persistence.*;
import java.sql.Timestamp;


@SuppressWarnings("serial")
@Entity
@Table(name = "MARKET_DATA")
@Proxy(lazy = false)
public class MarketData {
    public static final int EQUITY = 1;
    public static final int FUTURE = 2;

    private Long securityId;
    
    private Timestamp asOfDate;
    
    private BigDecimal price;
    
    private BigDecimal previousPrice;

    private LocalDateTime lastUpdate;

    @Transient
    private LocalDateTime receivedTime;

    @Transient
    private int dataType;

    public MarketData(Long securityId,
                      Timestamp asOfDate,
                      BigDecimal price,
                      BigDecimal previousPrice) {
        this.securityId = securityId;
        this.asOfDate = asOfDate;
        this.price = price;
        this.previousPrice = previousPrice;
        this.receivedTime = LocalDateTime.now();
        this.dataType = 0;
    }

    public MarketData(Long securityId,
                      Timestamp asOfDate,
                      BigDecimal price,
                      BigDecimal previousPrice,
                      int dataType) {
        this.securityId = securityId;
        this.asOfDate = asOfDate;
        this.price = price;
        this.previousPrice = previousPrice;
        this.receivedTime = LocalDateTime.now();
        this.dataType = dataType;
    }

    public MarketData(MarketData marketData) {
        this.securityId = marketData.getSecurityId();
        this.asOfDate = marketData.getAsOfDate();
        this.price = marketData.getPrice();
        this.previousPrice = marketData.getPreviousPrice();
        this.receivedTime = marketData.getReceivedTime();
        this.dataType = marketData.getDataType();
    }

    @Id
    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "AS_OF_DATE")
    public Timestamp getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Timestamp asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "PREVIOUS_PRICE")
    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(BigDecimal previousPrice) {
        this.previousPrice = previousPrice;
    }

    @Column(name = "LAST_UPDATE")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Transient
    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }

    @Transient
    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
