package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.AccountClassDao;
import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.dao.account.AccountValuationInitDao;
import com.datayes.invest.pms.dao.account.AccountValuationTypeDao;
import com.datayes.invest.pms.dao.account.CashTransactionDao;
import com.datayes.invest.pms.dao.account.FeeDao;
import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.dao.account.PositionClassDao;
import com.datayes.invest.pms.dao.account.PositionInitDao;
import com.datayes.invest.pms.dao.account.PositionValuationTypeDao;
import com.datayes.invest.pms.dao.account.PositionYieldDao;
import com.datayes.invest.pms.dao.account.RateTypeDao;
import com.datayes.invest.pms.dao.account.SecurityTransactionDao;
import com.datayes.invest.pms.dao.account.SourceTransactionDao;
import com.datayes.invest.pms.dao.account.SystemIdMappingDao;
import com.datayes.invest.pms.dao.account.TradeSideDao;
import com.datayes.invest.pms.dao.account.TransactionDao;
import com.datayes.invest.pms.dao.account.impl.AccountClassDaoImpl;
import com.datayes.invest.pms.dao.account.impl.AccountDaoImpl;
import com.datayes.invest.pms.dao.account.impl.AccountValuationInitDaoImpl;
import com.datayes.invest.pms.dao.account.impl.AccountValuationTypeDaoImpl;
import com.datayes.invest.pms.dao.account.impl.CashTransactionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.FeeDaoImpl;
import com.datayes.invest.pms.dao.account.impl.MarketDataDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionClassDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionInitDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionValuationTypeDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionYieldDaoImpl;
import com.datayes.invest.pms.dao.account.impl.RateTypeDaoImpl;
import com.datayes.invest.pms.dao.account.impl.SecurityTransactionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.SourceTransactionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.SystemIdMappingDaoImpl;
import com.datayes.invest.pms.dao.account.impl.TradeSideDaoImpl;
import com.datayes.invest.pms.dao.account.impl.TransactionDaoImpl;
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
        bind(PositionClassDao.class).to(PositionClassDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionInitDao.class).to(PositionInitDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionValuationTypeDao.class).to(PositionValuationTypeDaoImpl.class).in(Scopes.SINGLETON);
        bind(PositionYieldDao.class).to(PositionYieldDaoImpl.class).in(Scopes.SINGLETON);
        bind(RateTypeDao.class).to(RateTypeDaoImpl.class).in(Scopes.SINGLETON);
        bind(SecurityTransactionDao.class).to(SecurityTransactionDaoImpl.class).in(Scopes.SINGLETON);
        bind(SourceTransactionDao.class).to(SourceTransactionDaoImpl.class).in(Scopes.SINGLETON);
        bind(SystemIdMappingDao.class).to(SystemIdMappingDaoImpl.class).in(Scopes.SINGLETON);
        bind(TradeSideDao.class).to(TradeSideDaoImpl.class).in(Scopes.SINGLETON);
        bind(TransactionDao.class).to(TransactionDaoImpl.class).in(Scopes.SINGLETON);
    }
}
