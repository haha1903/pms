package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import com.datayes.invest.pms.dao.security.CountryDao;
import com.datayes.invest.pms.entity.security.Country;

public class CountryDaoImpl extends GenericSecurityMasterDaoImpl<Country, String> implements CountryDao {

    protected CountryDaoImpl() {
        super(Country.class);
    }

    public List<Country> findAll() {
        @SuppressWarnings("unchecked")
        List<Country> countryList = (List<Country>) enableCache(getEntityManager().createQuery(
            "from Country")).getResultList();
        return countryList;
    }
}
