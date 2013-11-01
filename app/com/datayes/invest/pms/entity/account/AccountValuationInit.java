package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@SuppressWarnings("serial")
@Entity
@Table(name = "ACCOUNT_VALUATION_INIT")
@Proxy(lazy = false)
public class AccountValuationInit extends EntityBase {
    private Long accountId;

    private Long typeId;

    private BigDecimal valueAmount;

    private AccountValuationInit() {
        // used by persistence library
    }

    public AccountValuationInit(Long accountId,
                                Long typeId,
                                BigDecimal valueAmount) {
        this.accountId = accountId;
        this.typeId = typeId;
        this.valueAmount = valueAmount;
    }

    @Id
    @Column(name = "ACCOUNT_ID")
    public Long getAccountId(){
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Id
    @Column(name = "TYPE_ID")
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(name = "VALUE_AMOUNT")
    public BigDecimal getValueAmount() {
        return valueAmount;
    }

    public void setValueAmount(BigDecimal valueAmount) {
        this.valueAmount = valueAmount;
    }
}

