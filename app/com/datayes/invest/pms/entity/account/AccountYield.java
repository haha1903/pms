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

@Entity
@Table(name = "ACCOUNT_YIELD")
@Proxy(lazy = false) 
public class AccountYield extends EntityBase {
    
    private Long id;
    
    private LocalDate asOfDate;
    
    private Long accountId;
    
    private Long assetItemId;
    
    private Character currencyTypeCode;
    
    private BigDecimal beginValueCamt;
    
    private BigDecimal endValueCamt;
    
    private BigDecimal inCamt;
    
    private BigDecimal outCamt;
    
    private BigDecimal earnLossCamt;
    
    private BigDecimal yieldRatio;
    
    private String lastChangeUserId;
    
    private LocalDate lastUpdate;
    
    private char isLocked;
    
    private AccountYield() {
        
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

    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Column(name = "ASSET_ITEM_ID")
    public Long getAssetItem() {
        return assetItemId;
    }

    public void setAssetItem(Long assetItem) {
        this.assetItemId = assetItem;
    }

    @Column(name = "CURRENCY_TYPE_CD")
    public Character getCurrencyTypeCode() {
        return currencyTypeCode;
    }

    public void setCurrencyTypeCode(Character currencyTypeCode) {
        this.currencyTypeCode = currencyTypeCode;
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

    @Column(name = "YIELD_RATIO")
    public BigDecimal getYieldRatio() {
        return yieldRatio;
    }

    public void setYieldRatio(BigDecimal yieldRatio) {
        this.yieldRatio = yieldRatio;
    }

    @Column(name = "LAST_CHG_USER_ID")
    public String getLastChangeUserId() {
        return lastChangeUserId;
    }

    public void setLastChangeUserId(String lastChangeUserId) {
        this.lastChangeUserId = lastChangeUserId;
    }

    @Column(name = "LAST_UPDATE")
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
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
