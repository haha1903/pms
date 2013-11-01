package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "RATE_TYPE")
@Proxy(lazy = false) 
public class RateType {
    
    private Long id;
    
    private String name;
    
    private String desc;
    
    private String exchangeCode;
    
    private Integer baseDays;

    @Id
    @Column(name = "RATE_TYPE_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "RATE_TYPE_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "RATE_TYPE_DESC")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Column(name = "EXCHANGE_CD")
    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    @Column(name = "BASEDAYS")
    public Integer getBaseDays() {
        return baseDays;
    }

    public void setBaseDays(Integer baseDays) {
        this.baseDays = baseDays;
    }
}
