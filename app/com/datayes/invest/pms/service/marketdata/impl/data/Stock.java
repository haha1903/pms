package com.datayes.invest.pms.service.marketdata.impl.data;


import java.io.Serializable;
import java.util.List;


public class Stock extends Head implements Serializable {
	private static final long serialVersionUID = 2299012709889314366L;
	
	private String ticker; // 证劵代码, 6
    private String shortNM; // 证劵简称, 8
    private Double prevClosePrice; // 昨收盘价格, 9,3
    private Double openPrice; // 今日开盘价, 9,3
    private Long volume; // 成交数量, 12,0
    private Double value; // 成交金额, 17,3
    private Integer deal; // 成交笔数, 9,0
    private Long cntrctPosition; // 合约持仓量, 12,0
    private Double highPrice; // 最高价格, 9,3
    private Double lowPrice; // 最低价格, 9,3
    private Double lastPrice; // 最新价格, 9,3
    private Double priceDelta1; // 价格升跌1, 9,3
    private Double priceDelta2; // 价格升跌2, 9,3
    private Double peRatio1; // 市盈率1, 7,2
    private Double peRatio2; // 市盈率2, 7,2
    private List<Book> bidBook; // 申买, 5
    private List<Book> askBook; // 申卖, 5
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getShortNM() {
		return shortNM;
	}
	public void setShortNM(String shortNM) {
		this.shortNM = shortNM;
	}
	public Double getPrevClosePrice() {
		return prevClosePrice;
	}
	public void setPrevClosePrice(Double prevClosePrice) {
		this.prevClosePrice = prevClosePrice;
	}
	public Double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
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
	public Integer getDeal() {
		return deal;
	}
	public void setDeal(Integer deal) {
		this.deal = deal;
	}
	public Long getCntrctPosition() {
		return cntrctPosition;
	}
	public void setCntrctPosition(Long cntrctPosition) {
		this.cntrctPosition = cntrctPosition;
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
	public Double getPriceDelta1() {
		return priceDelta1;
	}
	public void setPriceDelta1(Double priceDelta1) {
		this.priceDelta1 = priceDelta1;
	}
	public Double getPriceDelta2() {
		return priceDelta2;
	}
	public void setPriceDelta2(Double priceDelta2) {
		this.priceDelta2 = priceDelta2;
	}
	public Double getPeRatio1() {
		return peRatio1;
	}
	public void setPeRatio1(Double peRatio1) {
		this.peRatio1 = peRatio1;
	}
	public Double getPeRatio2() {
		return peRatio2;
	}
	public void setPeRatio2(Double peRatio2) {
		this.peRatio2 = peRatio2;
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
		return "Stock [ticker=" + ticker + ", shortNM=" + shortNM
				+ ", prevClosePrice=" + prevClosePrice + ", openPrice="
				+ openPrice + ", volume=" + volume + ", value=" + value
				+ ", deal=" + deal + ", cntrctPosition=" + cntrctPosition
				+ ", highPrice=" + highPrice + ", lowPrice=" + lowPrice
				+ ", lastPrice=" + lastPrice + ", priceDelta1=" + priceDelta1
				+ ", priceDelta2=" + priceDelta2 + ", peRatio1=" + peRatio1
				+ ", peRatio2=" + peRatio2 + ", bidBook=" + bidBook
				+ ", askBook=" + askBook + ", getTimestamp()=" + getTimestamp()
				+ ", getLocalTimestamp()=" + getLocalTimestamp()
				+ ", getSecID()=" + getSecID() + ", getDyID()=" + getDyID()
				+ ", getUtcOffset()=" + getUtcOffset() + ", getExchangeCD()="
				+ getExchangeCD() + ", getCurrencyCD()=" + getCurrencyCD()
				+ "]";
	}
}
