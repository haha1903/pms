package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "CASH_TRAN_TYPE")
@Proxy(lazy = false) 
public class CashTransactionType extends EntityBase {
    
    private String code;
    
    private String desc;

    @Id
    @Column(name = "CASH_TRAN_TYPE_CD")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "CASH_TRAN_TYPE_DESC")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
