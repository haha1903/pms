package com.datayes.invest.pms.service.marketdata.impl.data;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;


public class Future extends Head implements Serializable {
	private static final long serialVersionUID = 3719013051403849585L;
	
	private String ticker; // 证券代码, 6
	private String exchInstrID; // 交易所证券编码
	private Double preClosePrice; // 昨收盘价格, 9,3
	private Double preSettlePrice; // 昨结算价, 9,3
	private Long preSettlePosition; // 昨持仓量, 12
	private Double preDelta; // 昨虚实度, 8,3
    private Double openPrice; // 今日开盘价, 9,3
    private Double closePrice; // 今收盘价格, 9,3
    private Long volume; // 成交数量, 12,0
    private Double value; // 成交金额, 17,3
    private Double settlePrice; // 结算价, 9,3
    private Long cntrctPosition; // 合约持仓量, 12,0
    private Double delta; // 虚实度, 8,3
    private Double highPrice; // 最高价格, 9,3
    private Double lowPrice; // 最低价格, 9,3
    private Double lastPrice; // 最新价格, 9,3
    private Double avgPrice; // 当日均价, 9,3
    private Double upperLimitPrice; // 涨停板价, 9,3
    private Double lowerLimitPrice; // 跌停板价, 9,3
    private String bussDate; // 业务日期 - 期货夜盘, 10
    private List<Book> bidBook; // 申买, 5
	private List<Book> askBook; // 申卖, 5
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		if (!StringUtils.isBlank(ticker)) {
			this.ticker = ticker.trim();
		}
	}
	public String getExchInstrID() {
		return exchInstrID;
	}
	public void setExchInstrID(String exchInstrID) {
		if (!StringUtils.isBlank(exchInstrID)) {
			this.exchInstrID = exchInstrID.trim();
		}
	}
	public Double getPreClosePrice() {
		return preClosePrice;
	}
	public void setPreClosePrice(Double preClosePrice) {
		this.preClosePrice = preClosePrice;
	}
	public Double getPreSettlePrice() {
		return preSettlePrice;
	}
	public void setPreSettlePrice(Double preSettlePrice) {
		this.preSettlePrice = preSettlePrice;
	}
	public Long getPreSettlePosition() {
		return preSettlePosition;
	}
	public void setPreSettlePosition(Long preSettlePosition) {
		this.preSettlePosition = preSettlePosition;
	}
	public Double getPreDelta() {
		return preDelta;
	}
	public void setPreDelta(Double preDelta) {
		this.preDelta = preDelta;
	}
	public Double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getSettlePrice() {
		return settlePrice;
	}
	public void setSettlePrice(Double settlePrice) {
		this.settlePrice = settlePrice;
	}
	public Long getCntrctPosition() {
		return cntrctPosition;
	}
	public void setCntrctPosition(Long cntrctPosition) {
		this.cntrctPosition = cntrctPosition;
	}
	public Double getDelta() {
		return delta;
	}
	public void setDelta(Double delta) {
		this.delta = delta;
	}
	public Double getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}
	public Double getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public Double getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}
	public Double getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}
	public Double getUpperLimitPrice() {
		return upperLimitPrice;
	}
	public void setUpperLimitPrice(Double upperLimitPrice) {
		this.upperLimitPrice = upperLimitPrice;
	}
	public Double getLowerLimitPrice() {
		return lowerLimitPrice;
	}
	public void setLowerLimitPrice(Double lowerLimitPrice) {
		this.lowerLimitPrice = lowerLimitPrice;
	}
	public String getBussDate() {
		return bussDate;
	}
	public void setBussDate(String bussDate) {
		this.bussDate = bussDate;
	}
	public List<Book> getBidBook() {
		return bidBook;
	}
	public void setBidBook(List<Book> bidBook) {
		this.bidBook = bidBook;
	}
	public List<Book> getAskBook() {
		return askBook;
	}
	public void setAskBook(List<Book> askBook) {
		this.askBook = askBook;
	}
	
	@Override
	public String toString() {
		return "Future [ticker=" + ticker + ", exchInstrID=" + exchInstrID
				+ ", preClosePrice=" + preClosePrice + ", preSettlePrice="
				+ preSettlePrice + ", preSettlePosition=" + preSettlePosition
				+ ", preDelta=" + preDelta + ", openPrice=" + openPrice
				+ ", closePrice=" + closePrice + ", volume=" + volume
				+ ", value=" + value + ", settlePrice=" + settlePrice
				+ ", cntrctPosition=" + cntrctPosition + ", delta=" + delta
				+ ", highPrice=" + highPrice + ", lowPrice=" + lowPrice
				+ ", lastPrice=" + lastPrice + ", avgPrice=" + avgPrice
				+ ", upperLimitPrice=" + upperLimitPrice + ", lowerLimitPrice="
				+ lowerLimitPrice + ", bussDate=" + bussDate + ", bidBook="
				+ bidBook + ", askBook=" + askBook + ", getTimestamp()="
				+ getTimestamp() + ", getLocalTimestamp()="
				+ getLocalTimestamp() + ", getSecID()=" + getSecID()
				+ ", getDyID()=" + getDyID() + ", getUtcOffset()="
				+ getUtcOffset() + ", getExchangeCD()=" + getExchangeCD()
				+ ", getCurrencyCD()=" + getCurrencyCD() + "]";
	}
}
