package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions.asScalaBuffer

import org.joda.time.LocalDate

import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction

import javax.inject.Inject

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