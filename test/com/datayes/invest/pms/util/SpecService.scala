package com.datayes.invest.pms.util

import com.datayes.invest.pms.web.service.AccountService

/**
 * Created by changhai on 13-12-12.
 */
trait SpecService extends SpecDao {
  val accountService = new AccountService
  accountService.setValue("accountDao", accountDao)
}
