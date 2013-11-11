package com.datayes.invest.pms.service.industry.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.datayes.invest.pms.dao.security.IndustryDao;
import com.datayes.invest.pms.dao.security.InstitutionIndustryDao;
import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.entity.security.Industry;
import com.datayes.invest.pms.entity.security.InstitutionIndustry;
import com.datayes.invest.pms.entity.security.Security;
import com.datayes.invest.pms.util.DefaultValues;
import com.datayes.invest.pms.service.industry.IndustryService;

@Singleton
public class IndustryServiceImpl implements IndustryService {
    
    @Inject
    private IndustryDao industryDao;

    @Inject
    private InstitutionIndustryDao institutionIndustryDao;

    @Inject
    private SecurityDao securityDao;
    
    private ConcurrentMap<Long, String> cache = new ConcurrentHashMap<>();
    
    private static final int CLASS_LEVEL = 1;

    private static final String UNKNOWN = "未分类";

    @Override
    public List<String> getAvailableIndustries() {
        List<Industry> industries = industryDao.findByDataSourceIdClassLevel(DefaultValues.INDUSTRY_DATA_SOURCE_ID(), CLASS_LEVEL);
        List<String> results = new ArrayList<String>();
        for (Industry ind : industries) {
            results.add(ind.getName());
        }
        return results;
    }


    @Override
    public String getIndustryBySecurityId(Long securityId) {
        String industry = cache.get(securityId);
        if (industry == null) {
            industry = loadIndustryBySecurityId(securityId);
            if (industry == null) {
                industry = UNKNOWN;
            }
            cache.put(securityId, industry);
        }
        return industry;
    }

    private String loadIndustryBySecurityId(Long securityId) {
        Security security = securityDao.findById(securityId);
        Long partyId = security.getPartyId();
        if (partyId == null) {
            return null;
        }
        List<InstitutionIndustry> list = institutionIndustryDao.findByPartyIdDataSourceId(partyId, DefaultValues.INDUSTRY_DATA_SOURCE_ID());
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getLevel1IndustName();
    }

}