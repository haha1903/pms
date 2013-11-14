package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.AccountDao
import javax.inject.Inject
import com.datayes.invest.pms.entity.account.Account
import scala.collection.JavaConversions._
import com.datayes.invest.pms.persist.dsl.transaction

class AccountService extends Logging {

  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var accountDeleteHelper: AccountDeleteHelper = null
  
  def getAccountList(asOfDate: LocalDate): List[Account] = transaction {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    accounts.toList
  }

  def delete(accountId: Long): Unit = transaction {
    accountDeleteHelper.deleteAccount(accountId)
  }
}