package com.datayes.invest.pms.service.marketindex.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarketIndex {

	private final String id;
	
	private final Map<Long, MarketIndexComponent> components;

	public MarketIndex(String id, Map<Long, MarketIndexComponent> components) {
		this.id = id;
		this.components = copy(components);
	}
	
	private Map<Long, MarketIndexComponent> copy(Map<Long, MarketIndexComponent> comps) {
		Map<Long, MarketIndexComponent> map = new HashMap<>(comps);
		return Collections.unmodifiableMap(map);
	}

	public String getId() {
		return id;
	}

	public Map<Long, MarketIndexComponent> getComponents() {
		return components;
	}
}
