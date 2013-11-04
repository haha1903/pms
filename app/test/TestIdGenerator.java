package test;

import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.PersistService;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.play.AppModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestIdGenerator {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.initialize();
        
        AccountDao accountDao = injector.getInstance(AccountDao.class);
        PositionDao positionDao = injector.getInstance(PositionDao.class);
        
        Transaction tx = Persist.beginTransaction();
        Long maxId = positionDao.findLargestPositionId();
        System.out.println("maxId = " + maxId);
        tx.commit();
        
        System.out.println();
    }

}
