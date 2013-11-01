package com.datayes.invest.pms.service.industry;

import java.util.List;

public interface IndustryService {

    List<String> getAvailableIndustries();
    
    String getIndustryBySecurityId(Long securityId);
}
