package com.datayes.invest.pms.web.service

import javax.inject.Inject
import com.datayes.invest.pms.mgmt.AccountManager
import com.datayes.invest.pms.persist.dsl.transaction

class AccountMgmtService {

  @Inject
  private var accountManager: AccountManager = null

  def deleteAccount(accountId: Long): Unit = {
    transaction {
      accountManager.delete(accountId)
    }
  }
}
