package com.datayes.invest.pms.logic;

import com.datayes.invest.pms.logic.order.OrderManager;
import com.datayes.invest.pms.logic.order.impl.OrderManagerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class LogicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OrderManager.class).to(OrderManagerImpl.class).in(Scopes.SINGLETON);
    }
}
