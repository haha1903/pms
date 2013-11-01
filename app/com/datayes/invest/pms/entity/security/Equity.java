package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

@Entity
@Table(name = "equity")
@PrimaryKeyJoinColumn(name="SECURITY_ID")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class Equity extends Security {
    
    private String issueCurrency;
    
    private Integer typeCode;
    
    @Column(name = "ISSUE_CURRENCY")
    public String getIssueCurrency() {
        return issueCurrency;
    }

    private void setIssueCurrency(String issueCurrency) {
        this.issueCurrency = issueCurrency;
    }

    @Column(name = "EQUITY_TYPE")
    public Integer getTypeCode() {
        return typeCode;
    }

    private void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }
}
