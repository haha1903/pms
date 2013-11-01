package com.datayes.invest.pms.dao.security;

import java.util.List;

import com.datayes.invest.pms.entity.security.Country;

public interface CountryDao extends GenericSecurityMasterDao<Country, String> {
    
    public List<Country> findAll();
}
