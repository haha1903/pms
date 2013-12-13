package com.datayes.invest.pms.util

import com.datayes.invest.pms.web.service.AccountDataService

trait SpecService extends SpecDao {
  val accountDataService = new AccountDataService
  accountDataService.setValue("accountDao", accountDao)
  accountDataService.setValue("securityDao", securityDao)
  accountDataService.setValue("cashPositionDao", cashPositionDao)
  accountDataService.setValue("securityPositionDao", securityPositionDao)
  accountDataService.setValue("positionHistDao", positionHistDao)
  accountDataService.setValue("carryingValueHistDao", carryingValueHistDao)
}
