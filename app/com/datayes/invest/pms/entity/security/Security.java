package com.datayes.invest.pms.entity.security;

import javax.persistence.*;

import com.datayes.invest.pms.dbtype.AssetClass;
import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "security")
@Inheritance(strategy=InheritanceType.JOINED)
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class Security extends EntityBase {
    
    private Long id;

    private Integer assetClassId;

    private String name;
    
    private String nameAbbr;
    
    private Long partyId;
    
    private String tickerSymbol;
    
    private String exchangeCode;

    @Id
    @Column(name = "SECURITY_ID")
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ASSET_CLASS")
    public Integer getAssetClassId() {
        return assetClassId;
    }

    private void setAssetClassId(Integer assetClassId) {
        this.assetClassId = assetClassId;
    }

    @Column(name = "SECURITY_NAME")
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Column(name = "SECURITY_NAME_ABBR")
    public String getNameAbbr() {
        return nameAbbr;
    }

    private void setNameAbbr(String nameAbbr) {
        this.nameAbbr = nameAbbr;
    }

    @Column(name = "PARTY_ID")
    public Long getPartyId() {
        return partyId;
    }

    private void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    @Column(name = "TICKER_SYMBOL")
	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	@Column(name = "EXCHANGE_CD")
	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

    /*
     * The method getPmsAssetClass() is intended be overriden by sub-classes
     * It is used to denote the asset class of a security
     */
    @Transient
    public AssetClass getPmsAssetClass() {
        throw new UnsupportedOperationException("Please override this method in sub-classes");
    }
}
