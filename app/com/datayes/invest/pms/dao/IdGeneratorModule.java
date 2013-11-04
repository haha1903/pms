package com.datayes.invest.pms.dao;

import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.impl.IdGeneratorImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class IdGeneratorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IdGenerator.class).to(IdGeneratorImpl.class).in(Scopes.SINGLETON);
    }
}
