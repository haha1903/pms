package com.datayes.invest.pms.dao.security;

import java.util.List;

import com.datayes.invest.pms.entity.security.InstitutionIndustry;


public interface InstitutionIndustryDao {

    List<InstitutionIndustry> findByPartyIdDataSourceId(Long partyId, Integer dataSourceId);
}
