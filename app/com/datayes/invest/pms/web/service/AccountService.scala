package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.AccountDao
import javax.inject.Inject
import com.datayes.invest.pms.entity.account.Account
import scala.collection.JavaConversions._

class AccountService extends Logging {

  @Inject
  private var accountDao: AccountDao = null
  
  def getAccountList(asOfDate: LocalDate): List[Account] = {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    accounts.toList
  }
//    accounts.map { a =>
//      Account(
//        id = a.getId,
//        name = Option(a.getAccountName),
//        accountNo = Option(a.getAccountNo),
//        countryCode = a.getCountryCode,
//        currencyCode = a.getCurrencyCode,
//        classCode = a.getAccountClass,
//        openDate = Option(a.getOpenDate).map(p => LocalDateTime.fromDateFields(p.toDate))
//      )
//    }
//  }

}