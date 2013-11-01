package com.datayes.invest.pms.entity.account;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

public class SecurityOrder extends EntityBase {
    
    private Long orderId;
    
    private Long positionId;
    
    private Long orderSourceId;
    
    private String orderourceNm;
    
    private String sourceOrderId;
    
    private LocalDate orderOpen;
    
    private String orderStatus;
    
    private String securityNm;
    
    private String tradeOrderType;
    
    private String tradeType;
    
    private String side;
    
    private BigDecimal priceLimit;
    
    private BigDecimal priceAvg;
    
    private BigDecimal amountOpen;
    
    private BigDecimal amountFilled;
    
    private SecurityOrder() {
        // used by persistence library   
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(Long orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public String getOrderourceNm() {
        return orderourceNm;
    }

    public void setOrderourceNm(String orderourceNm) {
        this.orderourceNm = orderourceNm;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public LocalDate getOrderOpen() {
        return orderOpen;
    }

    public void setOrderOpen(LocalDate orderOpen) {
        this.orderOpen = orderOpen;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSecurityNm() {
        return securityNm;
    }

    public void setSecurityNm(String securityNm) {
        this.securityNm = securityNm;
    }

    public String getTradeOrderType() {
        return tradeOrderType;
    }

    public void setTradeOrderType(String tradeOrderType) {
        this.tradeOrderType = tradeOrderType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    public BigDecimal getPriceAvg() {
        return priceAvg;
    }

    public void setPriceAvg(BigDecimal priceAvg) {
        this.priceAvg = priceAvg;
    }

    public BigDecimal getAmountOpen() {
        return amountOpen;
    }

    public void setAmountOpen(BigDecimal amountOpen) {
        this.amountOpen = amountOpen;
    }

    public BigDecimal getAmountFilled() {
        return amountFilled;
    }

    public void setAmountFilled(BigDecimal amountFilled) {
        this.amountFilled = amountFilled;
    }

}
