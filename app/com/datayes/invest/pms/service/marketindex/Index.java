package com.datayes.invest.pms.service.marketindex;

import org.apache.commons.lang3.ObjectUtils;

public class Index {

	private final String id;
	
	private final String name;

	public Index(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof Index)) {
	        return false;
	    }
	    Index other = (Index) obj;
	    return ObjectUtils.equals(this.id, other.id) && ObjectUtils.equals(this.name, other.name);
	}
	
	@Override
	public String toString() {
	    return "Index [id=" + id + ", name=" + name + "]";
	}
}
