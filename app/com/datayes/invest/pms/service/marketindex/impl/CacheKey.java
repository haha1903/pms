package com.datayes.invest.pms.service.marketindex.impl;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.util.BeanUtil;

public class CacheKey {

	private String indexName;
	
	private LocalDate asOfDate;
	
	private CacheKey() {
		// used by BeanUtil
	}

	public CacheKey(String indexName, LocalDate asOfDate) {
		this.indexName = indexName;
		this.asOfDate = asOfDate;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	private void setIndexName(String name) {
		this.indexName = name;
	}

	public LocalDate getAsOfDate() {
		return asOfDate;
	}
	
	private void setAsOfDate(LocalDate asOfDate) {
		this.asOfDate = asOfDate;
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtil.equals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return BeanUtil.hashCode(this);
	}
	
	@Override
	public String toString() {
		return BeanUtil.toString(this);
	}
}
