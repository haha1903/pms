package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import com.datayes.invest.pms.dao.security.CurrencyDao;
import com.datayes.invest.pms.entity.security.Currency;

public class CurrencyDaoImpl extends GenericSecurityMasterDaoImpl<Currency, String> implements CurrencyDao {

    protected CurrencyDaoImpl() {
        super(Currency.class);
    }

    public List<Currency> findAll() {
        @SuppressWarnings("unchecked")
        List<Currency> currencyList = (List<Currency>) getEntityManager().createQuery(
            "from Currency").getResultList();
        return currencyList;
    }

}
