package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.OrderIdGenerator;
import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.dao.account.PositionYieldIdGenerator;
import com.datayes.invest.pms.dao.account.impl.OrderIdGeneratorImpl;
import com.datayes.invest.pms.dao.account.impl.PositionIdGeneratorImpl;
import com.datayes.invest.pms.dao.account.impl.PositionYieldIdGeneratorImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class IdGeneratorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OrderIdGenerator.class).to(OrderIdGeneratorImpl.class).in(Scopes.SINGLETON);
        bind(PositionIdGenerator.class).to(PositionIdGeneratorImpl.class).in(Scopes.SINGLETON);
        bind(PositionYieldIdGenerator.class).to(PositionYieldIdGeneratorImpl.class).in(Scopes.SINGLETON);
    }
}
