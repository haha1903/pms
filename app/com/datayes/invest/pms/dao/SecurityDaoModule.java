package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.security.CountryDao;
import com.datayes.invest.pms.dao.security.CurrencyDao;
import com.datayes.invest.pms.dao.security.EquityDao;
import com.datayes.invest.pms.dao.security.EquityDividendDao;
import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.dao.security.FutureDao;
import com.datayes.invest.pms.dao.security.FuturePriceVolumeDao;
import com.datayes.invest.pms.dao.security.IndustryDao;
import com.datayes.invest.pms.dao.security.InstitutionIndustryDao;
import com.datayes.invest.pms.dao.security.MarketIndexDao;
import com.datayes.invest.pms.dao.security.PriceVolumeDao;
import com.datayes.invest.pms.dao.security.RepoDao;
import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.dao.security.impl.CountryDaoImpl;
import com.datayes.invest.pms.dao.security.impl.CurrencyDaoImpl;
import com.datayes.invest.pms.dao.security.impl.EquityDaoImpl;
import com.datayes.invest.pms.dao.security.impl.EquityDividendDaoImpl;
import com.datayes.invest.pms.dao.security.impl.ExchangeCalendarDaoImpl;
import com.datayes.invest.pms.dao.security.impl.FutureDaoImpl;
import com.datayes.invest.pms.dao.security.impl.FuturePriceVolumeDaoImpl;
import com.datayes.invest.pms.dao.security.impl.IndustryDaoImpl;
import com.datayes.invest.pms.dao.security.impl.InstitutionIndustryDaoImpl;
import com.datayes.invest.pms.dao.security.impl.MarketIndexDaoImpl;
import com.datayes.invest.pms.dao.security.impl.PriceVolumeDaoImpl;
import com.datayes.invest.pms.dao.security.impl.RepoDaoImpl;
import com.datayes.invest.pms.dao.security.impl.SecurityDaoImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;


public class SecurityDaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CountryDao.class).to(CountryDaoImpl.class).in(Scopes.SINGLETON);
        bind(CurrencyDao.class).to(CurrencyDaoImpl.class).in(Scopes.SINGLETON);
        bind(EquityDao.class).to(EquityDaoImpl.class).in(Scopes.SINGLETON);
        bind(EquityDividendDao.class).to(EquityDividendDaoImpl.class).in(Scopes.SINGLETON);
        bind(ExchangeCalendarDao.class).to(ExchangeCalendarDaoImpl.class).in(Scopes.SINGLETON);
        bind(FutureDao.class).to(FutureDaoImpl.class).in(Scopes.SINGLETON);
        bind(FuturePriceVolumeDao.class).to(FuturePriceVolumeDaoImpl.class).in(Scopes.SINGLETON);
        bind(IndustryDao.class).to(IndustryDaoImpl.class).in(Scopes.SINGLETON);
        bind(InstitutionIndustryDao.class).to(InstitutionIndustryDaoImpl.class).in(Scopes.SINGLETON);
        bind(MarketIndexDao.class).to(MarketIndexDaoImpl.class).in(Scopes.SINGLETON);
        bind(PriceVolumeDao.class).to(PriceVolumeDaoImpl.class).in(Scopes.SINGLETON);
        bind(RepoDao.class).to(RepoDaoImpl.class).in(Scopes.SINGLETON);
        bind(SecurityDao.class).to(SecurityDaoImpl.class).in(Scopes.SINGLETON);
    }
}
