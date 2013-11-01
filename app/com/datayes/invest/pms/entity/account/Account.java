package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

import com.datayes.invest.pms.entity.EntityBase;


@Entity
@Table(name = "ACCOUNT")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Account extends EntityBase {
    
    private Long id;
    
    private String countryCode;
    
    private String currencyCode;

    private Long accountTypeId;

    private String accountClass;
    
    private String accountNo;
    
    private String accountName;
    
    private LocalDateTime openDate;
    
    @SuppressWarnings("unused")
    private Account() {
        // used by persistence library
    }

    public Account(String countryCode ,String currencyCode, String accountClass, Long accountTypeId,
        String accountNo, String accountName, LocalDateTime openDate) {
        
        this.countryCode = countryCode;
        this.currencyCode = currencyCode;
        this.accountClass = accountClass;
        this.accountTypeId = accountTypeId;
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.openDate = openDate;
    }

    @Id
    @Column(name = "ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long accountId) {
        this.id = accountId;
    }

    @Column(name = "COUNTRY_CD")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "ACCOUNT_TYPE_ID")
    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    @Column(name = "ACCOUNT_CLASS_CD")
    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }
    
    @Column(name = "ACCOUNT_NO")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Column(name = "ACCOUNT_NAME")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Column(name = "OPEN_DATE")
    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }
}