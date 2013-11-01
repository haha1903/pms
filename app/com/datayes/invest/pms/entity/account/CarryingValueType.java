package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "CARRYING_VALUE_TYPE")
@Proxy(lazy = false)
public class CarryingValueType extends EntityBase {
    
    private Long typeId;
    
    private String typeName;
    
    private String typeDesc;
    
    private CarryingValueType() {
        
    }

    @Id
    @Column(name = "TYPE_ID")
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(name = "TYPE_NAME")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Column(name = "TYPE_DESC")
    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

}
