package com.datayes.invest.pms.service.marketindex;

import com.datayes.invest.pms.util.BeanUtil;

public class Index {

	private final String id;
	
	private final String name;

    private final String tickerSymbol;

	public Index(String id, String name, String tickerSymbol) {
		this.id = id;
		this.name = name;
        this.tickerSymbol = tickerSymbol;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    @Override
	public boolean equals(Object obj) {
	    return BeanUtil.equals(this, obj);
	}
	
	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}
}
