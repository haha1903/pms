package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.AccountValuationHistDao;
import com.datayes.invest.pms.dao.account.CarryingValueHistDao;
import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.PositionHistDao;
import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.dao.account.impl.AccountValuationHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.CarryingValueHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.CashPositionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionValuationHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.SecurityPositionDaoImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AccountExtraDaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountValuationHistDao.class).to(AccountValuationHistDaoImpl.class).in(Scopes.SINGLETON);
        bind(CarryingValueHistDao.class).to(CarryingValueHistDaoImpl.class).in(Scopes.SINGLETON);
        bind(CashPositionDao.class).to(CashPositionDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionDao.class).to(PositionDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionHistDao.class).to(PositionHistDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionValuationHistDao.class).to(PositionValuationHistDaoImpl.class).in(Scopes.SINGLETON);
        bind(SecurityPositionDao.class).to(SecurityPositionDaoImpl.class).in(Scopes.SINGLETON);
    }
}
