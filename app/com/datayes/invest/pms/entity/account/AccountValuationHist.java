package com.datayes.invest.pms.entity.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;
import com.datayes.invest.pms.util.BeanUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "ACCOUNT_VALUATION_HIST")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccountValuationHist extends EntityBase {
    
    private Long accountId;
    
    private Long typeId;
    
    private LocalDate asOfDate;
    
    private BigDecimal valueAmount;
    
    private String currencyCode;
    
    private LocalDateTime adjustTs;
    
    @SuppressWarnings("unused")
    private AccountValuationHist() {
        // used by persistence library
    }
    
    public AccountValuationHist(Long _accountId, Long _typeId, LocalDate _asOfDate, BigDecimal _valueAmount,
                    String _currencyCode, LocalDateTime _adjustTs) {
        this.accountId = _accountId;
        this.typeId = _typeId;
        this.asOfDate = _asOfDate;
        this.valueAmount = _valueAmount;
        this.currencyCode = _currencyCode;
        this.adjustTs = _adjustTs;
    }
    
    public AccountValuationHist(AccountValuationHist accountValuationHist) {
        this.accountId = accountValuationHist.getAccountId();
        this.typeId = accountValuationHist.getTypeId();
        this.asOfDate = accountValuationHist.getAsOfDate();
        this.valueAmount = accountValuationHist.getValueAmount();
        this.currencyCode = accountValuationHist.getCurrencyCode();
        this.adjustTs = accountValuationHist.getAdjustTs();
    }

    @Id
    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Id
    @Column(name = "ACC_VAL_TYPE_ID")
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Id
    @Column(name = "AS_OF_DATE")
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "VALUE_AMOUNT")
    public BigDecimal getValueAmount() {
        return valueAmount;
    }

    public void setValueAmount(BigDecimal valueAmount) {
        this.valueAmount = valueAmount;
    }

    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "ADJUST_TS")
    public LocalDateTime getAdjustTs() {
        return adjustTs;
    }

    public void setAdjustTs(LocalDateTime adjustTs) {
        this.adjustTs = adjustTs;
    }


    /*
     * Composite key
     */

    @Embeddable
    public class PK implements Serializable {

        private Long accountId;

        private Long typeId;

        private LocalDate asOfDate;
        
        private PK() {
        }

        public PK(Long accountId, Long typeId, LocalDate asOfDate) {
            this.accountId = accountId;
            this.typeId = typeId;
            this.asOfDate = asOfDate;
        }

        @Column(name = "ACCOUNT_ID")
        public Long getAccountId() {
            return accountId;
        }

        private void setAccountId(Long positionId) {
            this.accountId = positionId;
        }

        @Column(name = "ACC_VAL_TYPE_ID")
        public Long getTypeId() {
            return typeId;
        }

        private void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        @Column(name = "AS_OF_DATE")
        public LocalDate getAsOfDate() {
            return asOfDate;
        }

        private void setAsOfDate(LocalDate asOfDate) {
            this.asOfDate = asOfDate;
        }

        @Override
        public boolean equals(Object obj) {
            return BeanUtil.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return BeanUtil.hashCode(this);
        }
    }
}
