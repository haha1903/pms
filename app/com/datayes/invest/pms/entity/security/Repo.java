package com.datayes.invest.pms.entity.security;


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

	private Integer days;

	//@Column(name = "INTEREST_BASEDAYS")
	public Integer getBaseDays() {
		return baseDays;
	}

	public void setBaseDays(Integer baseDays) {
		this.baseDays = baseDays;
	}

	//@Column(name = "DAYS")
	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
}
