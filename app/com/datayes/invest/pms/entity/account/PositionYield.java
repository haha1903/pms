package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

import java.sql.Timestamp;

@Entity
@Table(name = "POSITION_YIELD")
@Proxy(lazy = false) 
public class PositionYield extends EntityBase {
    
    private Long id;
    
    private LocalDate asOfDate;
    
    private Long positionId;
    
    private Long accountId;
    
    private Long subAccountId;
    
    private Long securityId;
    
    private Character currencyTypeCode;
    
    private String currencyCode;
    
    private BigDecimal positionCarryingValue;
    
    private BigDecimal securityCarryingValue;
    
    private BigDecimal dailyInterestCamt;
    
    private BigDecimal dividendCamt;
    
    private BigDecimal incrementCamt;
    
    private BigDecimal priceDiffEarnCamt;
    
    private BigDecimal beginValueCamt;
    
    private BigDecimal endValueCamt;
    
    private BigDecimal inCamt;
    
    private BigDecimal outCamt;
    
    private BigDecimal earnLossCamt;

    private BigDecimal tradeEarnCamt;
    
    private String lastChangeUserId;

    private Timestamp lastUpdate;
    
    private char isLocked;
    
    private PositionYield() {
        
    }

    public PositionYield(LocalDate asOfDate, Long positionId, Long accountId, Long securityId, Character currencyTypeCode, String currencyCode, BigDecimal positionCarryingValue, BigDecimal securityCarryingValue, BigDecimal dailyInterestCamt, BigDecimal dividendCamt, BigDecimal priceDiffEarnCamt, BigDecimal incrementCamt, BigDecimal beginValueCamt, BigDecimal endValueCamt, BigDecimal inCamt, BigDecimal outCamt, BigDecimal earnLossCamt, BigDecimal tradeEarnCamt, String lastChangeUserId, char locked) {
        this.asOfDate = asOfDate;
        this.positionId = positionId;
        this.accountId = accountId;
        this.securityId = securityId;
        this.currencyTypeCode = currencyTypeCode;
        this.currencyCode = currencyCode;
        this.positionCarryingValue = positionCarryingValue;
        this.securityCarryingValue = securityCarryingValue;
        this.dailyInterestCamt = dailyInterestCamt;
        this.dividendCamt = dividendCamt;
        this.priceDiffEarnCamt = priceDiffEarnCamt;
        this.incrementCamt = incrementCamt;
        this.beginValueCamt = beginValueCamt;
        this.endValueCamt = endValueCamt;
        this.inCamt = inCamt;
        this.outCamt = outCamt;
        this.earnLossCamt = earnLossCamt;
        this.tradeEarnCamt = tradeEarnCamt;
        this.lastChangeUserId = lastChangeUserId;
        this.isLocked = locked;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "AS_OF_DATE")
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "POSITION_ID")
    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Column(name = "SUB_ACCOUNT_ID")
    public Long getSubAccountId() {
        return subAccountId;
    }

    public void setSubAccountId(Long subAccountId) {
        this.subAccountId = subAccountId;
    }

    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "CURRENCY_TYPE_CD")
    public Character getCurrencyTypeCode() {
        return currencyTypeCode;
    }

    public void setCurrencyTypeCode(Character currencyTypeCode) {
        this.currencyTypeCode = currencyTypeCode;
    }

    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "POSITION_CARRYING_VALUE")
    public BigDecimal getPositionCarryingValue() {
        return positionCarryingValue;
    }

    public void setPositionCarryingValue(BigDecimal positionCarryingValue) {
        this.positionCarryingValue = positionCarryingValue;
    }

    @Column(name = "SECURITY_CARRYING_VALUE")
    public BigDecimal getSecurityCarryingValue() {
        return securityCarryingValue;
    }

    public void setSecurityCarryingValue(BigDecimal securityCarryingValue) {
        this.securityCarryingValue = securityCarryingValue;
    }

    @Column(name = "DAILY_INTEREST_CAMT")
    public BigDecimal getDailyInterestCamt() {
        return dailyInterestCamt;
    }

    public void setDailyInterestCamt(BigDecimal dailyInterestCamt) {
        this.dailyInterestCamt = dailyInterestCamt;
    }

    @Column(name = "DIVIDEND_CAMT")
    public BigDecimal getDividendCamt() {
        return dividendCamt;
    }

    public void setDividendCamt(BigDecimal dividendCamt) {
        this.dividendCamt = dividendCamt;
    }

    @Column(name = "INCREMENT_CAMT")
    public BigDecimal getIncrementCamt() {
        return incrementCamt;
    }

    public void setIncrementCamt(BigDecimal incrementCamt) {
        this.incrementCamt = incrementCamt;
    }

    @Column(name = "PRICE_DIFF_EARN_CAMT")
    public BigDecimal getPriceDiffEarnCamt() {
        return priceDiffEarnCamt;
    }

    public void setPriceDiffEarnCamt(BigDecimal priceDiffEarnCamt) {
        this.priceDiffEarnCamt = priceDiffEarnCamt;
    }

    @Column(name = "BEGIN_VALUE_CAMT")
    public BigDecimal getBeginValueCamt() {
        return beginValueCamt;
    }

    public void setBeginValueCamt(BigDecimal beginValueCamt) {
        this.beginValueCamt = beginValueCamt;
    }

    @Column(name = "END_VALUE_CAMT")
    public BigDecimal getEndValueCamt() {
        return endValueCamt;
    }

    public void setEndValueCamt(BigDecimal endValueCamt) {
        this.endValueCamt = endValueCamt;
    }

    @Column(name = "IN_CAMT")
    public BigDecimal getInCamt() {
        return inCamt;
    }

    public void setInCamt(BigDecimal inCamt) {
        this.inCamt = inCamt;
    }

    @Column(name = "OUT_CAMT")
    public BigDecimal getOutCamt() {
        return outCamt;
    }

    public void setOutCamt(BigDecimal outCamt) {
        this.outCamt = outCamt;
    }

    @Column(name = "EARN_LOSS_CAMT")
    public BigDecimal getEarnLossCamt() {
        return earnLossCamt;
    }

    public void setEarnLossCamt(BigDecimal earnLossCamt) {
        this.earnLossCamt = earnLossCamt;
    }

    @Column(name = "TRADE_EARN_CAMT")
    public BigDecimal getTradeEarnCamt() {
        return tradeEarnCamt;
    }

    public void setTradeEarnCamt(BigDecimal tradeEarnCamt) {
        this.tradeEarnCamt = tradeEarnCamt;
    }

    @Column(name = "LAST_CHANGE_USER_ID")
    public String getLastChangeUserId() {
        return lastChangeUserId;
    }

    public void setLastChangeUserId(String lastChangeUserId) {
        this.lastChangeUserId = lastChangeUserId;
    }

    @Column(name = "LAST_UPDATE")
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "IS_LOCKED")
    public char getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(char isLocked) {
        this.isLocked = isLocked;
    }
    
}
