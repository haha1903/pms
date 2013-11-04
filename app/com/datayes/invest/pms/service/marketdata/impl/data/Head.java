package com.datayes.invest.pms.service.marketdata.impl.data;


import java.io.Serializable;

public class Head implements Serializable {
	private static final long serialVersionUID = 9134250227585446344L;
	
	private Long timestamp; // 交易所时间
	private Long localTimestamp; // 本地接收时间
	private Long secID; // 通联数据证券编码, 12,0
	private String dyID; // 通联数据证券代码, 12
	private String utcOffset; // UTC 时间偏移, 6
	private String exchangeCD; // 交易所代码 - ISO10383, 4
	private String currencyCD; // 货币代码 - ISO4217, 3
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Long getLocalTimestamp() {
		return localTimestamp;
	}
	public void setLocalTimestamp(Long localTimestamp) {
		this.localTimestamp = localTimestamp;
	}
	public Long getSecID() {
		return secID;
	}
	public void setSecID(Long secID) {
		this.secID = secID;
	}
	public String getDyID() {
		return dyID;
	}
	public void setDyID(String dyID) {
		this.dyID = dyID;
	}
	public String getUtcOffset() {
		return utcOffset;
	}
	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}
	public String getExchangeCD() {
		return exchangeCD;
	}
	public void setExchangeCD(String exchangeCD) {
		this.exchangeCD = exchangeCD;
	}
	public String getCurrencyCD() {
		return currencyCD;
	}
	public void setCurrencyCD(String currencyCD) {
		this.currencyCD = currencyCD;
	}
}
