package com.datayes.invest.pms.service.marketdata.impl.data;

import java.io.Serializable;

public class Book implements Serializable {
	private static final long serialVersionUID = -556273473870883971L;
	
	private Double price; // 价格, 9,3
	private Long volume; // 数量, 12,0
	
	public Book() {
	}
	public Book(Double price, Long volume) {
		this.price = price;
		this.volume = volume;
	}
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	
	@Override
	public String toString() {
		return "Book [price=" + price + ", volume=" + volume + "]";
	}

}
