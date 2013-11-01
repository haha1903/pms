package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "industry")
@Proxy(lazy = false)
public class Industry extends EntityBase {
    
    private Integer id;
    
    private String name;
    
    private Integer classLevel;
    
    private  Integer dataSourceId;
    
    private Industry() {
        
    }

    @Id
    @Column(name = "INDUSTRY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "INDUSTRY_NM")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Column(name = "CLASS_LEVEL")
    public Integer getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(Integer classLevel) {
        this.classLevel = classLevel;
    }

    @Column(name = "DATA_SOURCE_ID")
    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
}
