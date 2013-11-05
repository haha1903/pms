package com.datayes.invest.pms.play;

import com.datayes.invest.pms.dao.AccountBaseDaoModule;
import com.datayes.invest.pms.dao.AccountExtraDaoCacheModule;
import com.datayes.invest.pms.dao.AccountExtraDaoModule;
import com.datayes.invest.pms.dao.IdGeneratorModule;
import com.datayes.invest.pms.dao.SecurityDaoModule;
import com.datayes.invest.pms.persist.PersistModule;
import com.datayes.invest.pms.persist.PersistService;
import com.datayes.invest.pms.service.ServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Injectors {
    
    private Injector injector;
    
    private Injector importerInjector;

    public Injectors() {
        Injector baseInjector = Guice.createInjector(
            new AccountBaseDaoModule(),
            new IdGeneratorModule(),
            new PersistModule(),
            new SecurityDaoModule(),
            new ServiceModule());
        
        injector = baseInjector.createChildInjector(
            new AccountExtraDaoModule());
        
        importerInjector = baseInjector.createChildInjector(
            new AccountExtraDaoCacheModule());
        
        initialize();
    }
    
    private void initialize() {
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.initialize();
    }
    
    public Injector getInjector() {
        return injector;
    }
    
    public Injector getImporterInjector() {
        return importerInjector;
    }
}
