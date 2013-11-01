package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;


@Entity
@Table(name = "currency")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class Currency extends EntityBase {
    
    private String currencyCode;
    
    private String currencyName;
    
    private Currency() {
        // used by persistence library   
    }


    @Id
    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    private void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "CURRENCY_NAME")
    public String getCurrencyName() {
        return currencyName;
    }

    private void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
