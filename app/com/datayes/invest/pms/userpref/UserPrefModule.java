package com.datayes.invest.pms.userpref;

import com.datayes.invest.pms.userpref.impl.UserPrefImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class UserPrefModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserPref.class).to(UserPrefImpl.class).in(Scopes.SINGLETON);
    }
}
