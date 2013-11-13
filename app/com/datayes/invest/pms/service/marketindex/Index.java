package com.datayes.invest.pms.service.marketindex;

import org.apache.commons.lang3.ObjectUtils;

public class Index {

	private final String id;
	
	private final String desc;

	public Index(String id, String desc) {
		this.id = id;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (! (obj instanceof Index)) {
	        return false;
	    }
	    Index other = (Index) obj;
	    return ObjectUtils.equals(this.id, other.id) && ObjectUtils.equals(this.desc, other.desc);
	}
	
	@Override
	public String toString() {
	    return "Index [id=" + id + ", desc=" + desc + "]";
	}
}
