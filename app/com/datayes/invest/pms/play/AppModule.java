package com.datayes.invest.pms.play;

import com.datayes.invest.pms.dao.AccountDaoModule;
import com.datayes.invest.pms.dao.SecurityDaoModule;
import com.datayes.invest.pms.persist.PersistModule;
import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        // DAO
        install(new AccountDaoModule());
        install(new SecurityDaoModule());
        
        // Persist
        install(new PersistModule());
    }

}
