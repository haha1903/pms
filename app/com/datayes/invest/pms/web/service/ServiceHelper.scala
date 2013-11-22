package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.entity.account.Account
import play.pms.ClientException
import org.joda.time.LocalDate
import javax.inject.Inject

class ServiceHelper {
  
  @Inject
  private var accountDao: AccountDao = null
  
  def loadAccount(accountId: Long, asOfDate: LocalDate): Account = {
    val account = accountDao.findById(accountId)
    if (account == null) {
      throw new ClientException("Account (id = " + accountId + ") does not exist", null)
    }
    if (asOfDate != null && account.getOpenDate() != null) {
      if (asOfDate.isBefore(account.getOpenDate().toLocalDate())) {
        throw new ClientException("Account (id = " + accountId + ") does not exist for " + asOfDate, null)
      }
    }
    account
  }
}