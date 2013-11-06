package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "institution_industry")
@Proxy(lazy = false)
public class InstitutionIndustry extends EntityBase {
    
    private Long partyId;
    
    private Integer dataSourceId;
    
    private Character isCurrent;
    
    private String level1IndustName;
    
    private InstitutionIndustry() {
        
    }

    @Id
    @Column(name = "PARTY_ID")
    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }
    
    @Column(name = "DATA_SOURCE_ID")
    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Column(name = "IS_CURRENT")
    public char getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(char isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Column(name = "LEVEL1_INDUST_NM")
    public String getLevel1IndustName() {
        return level1IndustName;
    }

    public void setLevel1IndustName(String level1IndustName) {
        this.level1IndustName = level1IndustName;
    }
}
