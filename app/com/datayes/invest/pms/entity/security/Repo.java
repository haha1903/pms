package com.datayes.invest.pms.entity.security;


import com.datayes.invest.pms.dbtype.AssetClass;

/*
TODO This class is not used yet
@Entity
@Table(name = "equity")
@PrimaryKeyJoinColumn(name = "SECURITY_ID")
@Proxy(lazy = false)
@SuppressWarnings("unused")
*/
public class Repo extends Security {

	private Integer baseDays;

	private Integer maturity;

	//@Column(name = "INTEREST_BASEDAYS")
	public Integer getBaseDays() {
		return baseDays;
	}

	public void setBaseDays(Integer baseDays) {
		this.baseDays = baseDays;
	}

	//@Column(name = "MATURITY")
	public Integer getMaturity() {
		return maturity;
	}

	public void setMaturity(Integer maturity) {
		this.maturity = maturity;
	}

    @Override
    public AssetClass getPmsAssetClass() {
        return AssetClass.REPO;
    }
}
