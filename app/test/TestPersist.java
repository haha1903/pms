package test;

import com.datayes.invest.pms.dao.AccountDaoModule;
import com.datayes.invest.pms.dao.SecurityDaoModule;
import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.entity.account.Account;
import com.datayes.invest.pms.entity.security.ExchangeCalendar;
import com.datayes.invest.pms.entity.security.Security;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.PersistModule;
import com.datayes.invest.pms.persist.PersistService;
import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.Transaction;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.joda.time.LocalDate;

public class TestPersist {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new PersistModule(), new AccountDaoModule(), new SecurityDaoModule());
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.initialize();
        
        AccountDao accountDao = injector.getInstance(AccountDao.class);
        SecurityDao securityDao = injector.getInstance(SecurityDao.class);

        ExchangeCalendarDao exchangeCalendarDao = injector.getInstance(ExchangeCalendarDao.class);
        
        Transaction tx1 = Persist.beginTransaction();
        
        Account account = accountDao.findById(1L);
        account.setAccountNo("123123123");
        accountDao.update(account);
        System.out.println(account);
        
        Transaction tx2 = Persist.beginTransaction(PersistUnit.SECURITY_MASTER);
        
        Security security = securityDao.findById(1L);
        ExchangeCalendar exchangeCalendar = exchangeCalendarDao.findPreviousDaysByExchangeCode(new LocalDate(2013, 9, 19), "XSHG", 1).get(0);
        //ExchangeCalendar exchangeCalendar = exchangeCalendarDao.findByDateExchangeCode(new LocalDate(2013, 9, 18), "XSHG");
        System.out.println(exchangeCalendar.getDate() + ", " + exchangeCalendar.getExchangeCode() + ", " + exchangeCalendar.isTradeHoliday());

        System.out.println(security);
        
        tx2.commit();
        
        tx1.commit();
    }
}
