package com.datayes.invest.pms.dao.security;

import java.util.List;

import com.datayes.invest.pms.entity.security.Currency;

public interface CurrencyDao extends GenericSecurityMasterDao<Currency, String> {
    
    public List<Currency> findAll();
}
