package com.datayes.invest.pms.service.marketindex.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarketIndex {

	private final String name;
	
	private final Map<Long, MarketIndexComponent> components;

	public MarketIndex(String name, Map<Long, MarketIndexComponent> components) {
		this.name = name;
		this.components = copy(components);
	}
	
	private Map<Long, MarketIndexComponent> copy(Map<Long, MarketIndexComponent> comps) {
		Map<Long, MarketIndexComponent> map = new HashMap<>(comps);
		return Collections.unmodifiableMap(map);
	}

	public String getName() {
		return name;
	}

	public Map<Long, MarketIndexComponent> getComponents() {
		return components;
	}
}
