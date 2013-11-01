package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "TRADE_SIDE")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TradeSide extends EntityBase {
    
    private String code;
    
    private String desc;

    @Id
    @Column(name = "TRADE_SIDE_CD")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "TRADE_SIDE_DESC")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
