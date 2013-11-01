package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "country")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class Country extends EntityBase {
    
    private String countryCode;
    
    private String countryName;
    
    private Country() {
        // used by persistence library
    }

    @Id
    @Column(name = "COUNTRY_CD")
    public String getCountryCode() {
        return countryCode;
    }

    private void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "COUNTRY_NAME")
    public String getCountryName() {
        return countryName;
    }

    private void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
