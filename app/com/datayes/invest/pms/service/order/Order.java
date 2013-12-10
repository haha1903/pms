package com.datayes.invest.pms.service.order;

import com.datayes.invest.pms.dbtype.TradeSide;
import org.joda.time.LocalTime;

import java.math.BigDecimal;

public class Order {

    private Long orderId;

    private Long accountId;

    private Long securityId;

    private Long amount;

    private TradeSide tradeSide;

    private BigDecimal priceLimit;

    private BigDecimal priceGuideline;

    private Boolean stpFlag;

    private String stpAlgorithm;

    private LocalTime stpStartTime;

    private LocalTime stpEndTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public TradeSide getTradeSide() {
        return tradeSide;
    }

    public void setTradeSide(TradeSide tradeSide) {
        this.tradeSide = tradeSide;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    public BigDecimal getPriceGuideline() {
        return priceGuideline;
    }

    public void setPriceGuideline(BigDecimal priceGuideline) {
        this.priceGuideline = priceGuideline;
    }

    public Boolean isStpFlag() {
        return stpFlag;
    }

    public void setStpFlag(Boolean stpFlag) {
        this.stpFlag = stpFlag;
    }

    public String getStpAlgorithm() {
        return stpAlgorithm;
    }

    public void setStpAlgorithm(String stpAlgorithm) {
        this.stpAlgorithm = stpAlgorithm;
    }

    public LocalTime getStpStartTime() {
        return stpStartTime;
    }

    public void setStpStartTime(LocalTime stpStartTime) {
        this.stpStartTime = stpStartTime;
    }

    public LocalTime getStpEndTime() {
        return stpEndTime;
    }

    public void setStpEndTime(LocalTime stpEndTime) {
        this.stpEndTime = stpEndTime;
    }
}
