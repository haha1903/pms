package com.datayes.invest.pms.persist;

import com.datayes.invest.pms.persist.hibernate.PersistServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class PersistModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistService.class).to(PersistServiceImpl.class).in(Scopes.SINGLETON);
    }
}
