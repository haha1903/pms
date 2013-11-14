package com.datayes.invest.pms.service.industry;

import java.util.List;

public interface IndustryService {
	
    List<String> getIndustries();
    
    String getIndustryBySecurityId(Long securityId);
}
