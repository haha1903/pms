package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.AccountValuationHistDao;
import com.datayes.invest.pms.dao.account.CarryingValueHistDao;
import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.PositionHistDao;
import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.dao.account.cacheimpl.AccountValuationHistDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.CarryingValueHistDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.CashPositionDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.PositionDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.PositionHistDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.PositionValuationHistDaoCacheImpl;
import com.datayes.invest.pms.dao.account.cacheimpl.SecurityPositionDaoCacheImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AccountExtraDaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountValuationHistDao.class).to(AccountValuationHistDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(CarryingValueHistDao.class).to(CarryingValueHistDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(CashPositionDao.class).to(CashPositionDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(PositionDao.class).to(PositionDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(PositionHistDao.class).to(PositionHistDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(PositionValuationHistDao.class).to(PositionValuationHistDaoCacheImpl.class).in(Scopes.SINGLETON);
        bind(SecurityPositionDao.class).to(SecurityPositionDaoCacheImpl.class).in(Scopes.SINGLETON);
    }

}
