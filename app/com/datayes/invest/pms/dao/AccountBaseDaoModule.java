package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.*;
import com.datayes.invest.pms.dao.account.impl.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AccountBaseDaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountClassDao.class).to(AccountClassDaoImpl.class).in(Scopes.SINGLETON);
        bind(AccountDao.class).to(AccountDaoImpl.class).in(Scopes.SINGLETON);
        bind(AccountValuationInitDao.class).to(AccountValuationInitDaoImpl.class).in(Scopes.SINGLETON);
        bind(AccountValuationTypeDao.class).to(AccountValuationTypeDaoImpl.class).in(Scopes.SINGLETON);
        bind(CashTransactionDao.class).to(CashTransactionDaoImpl.class).in(Scopes.SINGLETON);
        bind(FeeDao.class).to(FeeDaoImpl.class).in(Scopes.SINGLETON);
        bind(MarketDataDao.class).to(MarketDataDaoImpl.class).in(Scopes.SINGLETON);
        bind(OrderDao.class).to(OrderDaoImpl.class).in(Scopes.SINGLETON);
        bind(OrderBasketDao.class).to(OrderBasketDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionClassDao.class).to(PositionClassDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionInitDao.class).to(PositionInitDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionValuationTypeDao.class).to(PositionValuationTypeDaoImpl.class).in(Scopes.SINGLETON);
        bind(RateTypeDao.class).to(RateTypeDaoImpl.class).in(Scopes.SINGLETON);
        bind(SecurityTransactionDao.class).to(SecurityTransactionDaoImpl.class).in(Scopes.SINGLETON);
        bind(SourceTransactionDao.class).to(SourceTransactionDaoImpl.class).in(Scopes.SINGLETON);
        bind(SystemIdMappingDao.class).to(SystemIdMappingDaoImpl.class).in(Scopes.SINGLETON);
        bind(TradeSideDao.class).to(TradeSideDaoImpl.class).in(Scopes.SINGLETON);
        bind(TransactionDao.class).to(TransactionDaoImpl.class).in(Scopes.SINGLETON);
    }
}
