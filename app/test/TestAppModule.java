package test;

import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.play.AppModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestAppModule {

    public static void main(String[] args) {
        
        Module appModule = new AppModule();
        
        Injector injector1 = Guice.createInjector(new AppModule());
        SecurityDao securityDao1 = injector1.getInstance(SecurityDao.class);
        System.out.println(securityDao1);
        
        Injector injector2 = Guice.createInjector(new AppModule());
        SecurityDao securityDao2 = injector1.getInstance(SecurityDao.class);
        System.out.println(securityDao2);
    }

}
