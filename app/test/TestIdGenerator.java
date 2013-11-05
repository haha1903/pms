package test;

import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.play.SystemInjectors;
import com.google.inject.Injector;

public class TestIdGenerator {

    public static void main(String[] args) {
        SystemInjectors injectors = SystemInjectors.INSTANCE;
        Injector injector = injectors.getInjector();
        
        AccountDao accountDao = injector.getInstance(AccountDao.class);
        IdGenerator idGenerator = injector.getInstance(IdGenerator.class);
        
        Transaction tx = Persist.beginTransaction();
        Long maxId = idGenerator.getNextPositionId();
        System.out.println("maxId = " + maxId);
        tx.commit();
        
        System.out.println();
    }

}
