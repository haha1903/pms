package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "POSITION_CLASS")
@Proxy(lazy = false) 
public class PositionClass extends EntityBase {
    
    private String code;
    
    private String desc;
    
    private PositionClass() {
        // used by persistence library   
    }

    @Id
    @Column(name = "POSITION_CLASS_CD")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "POSITION_CLASS_DESC")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
