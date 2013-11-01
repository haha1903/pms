package com.datayes.invest.pms.entity.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "fut_pricevolume")
@Proxy(lazy = false)
@SuppressWarnings({ "unused", "serial" })
public class FuturePriceVolume extends EntityBase implements Serializable {

    private Long id;

    private Long securityId;

    private LocalDate tradeDate;

    private BigDecimal priceOpen;

    private BigDecimal priceClose;

    private BigDecimal priceHigh;

    private BigDecimal priceLow;

    private BigDecimal pricePreviousClose;

    private BigDecimal priceSettle;

    private FuturePriceVolume() {
        // used by persistence
    }


    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Column(name = "security_id")
    public Long getSecurityId() {
        return securityId;
    }

    private void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Id
    @Column(name = "trade_date")
    public LocalDate getTradeDate() {
        return tradeDate;
    }

    private void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Column(name = "price_open")
    public BigDecimal getPriceOpen() {
        return priceOpen;
    }

    private void setPriceOpen(BigDecimal priceOpen) {
        this.priceOpen = priceOpen;
    }

    @Column(name = "price_close")
    public BigDecimal getPriceClose() {
        return priceClose;
    }

    private void setPriceClose(BigDecimal priceClose) {
        this.priceClose = priceClose;
    }

    @Column(name = "price_high")
    public BigDecimal getPriceHigh() {
        return priceHigh;
    }

    private void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }

    @Column(name = "price_low")
    public BigDecimal getPriceLow() {
        return priceLow;
    }

    private void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    @Column(name = "price_settle")
    public BigDecimal getPriceSettle() {
        return priceSettle;
    }

    private void setPriceSettle(BigDecimal priceSettle) {
        this.priceSettle = priceSettle;
    }

    @Column(name = "price_previous_close")
    public BigDecimal getPricePreviousClose() {
        return pricePreviousClose;
    }

    private void setPricePreviousClose(BigDecimal pricePreviousClose) {
        this.pricePreviousClose = pricePreviousClose;
    }
}
