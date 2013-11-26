package com.datayes.invest.pms.logic.positionyield

import com.datayes.invest.pms.entity.account.Account
import org.joda.time.LocalDate
import javax.inject.Inject


trait PositionYieldLogic {
  def process(account: Account, asOfData: LocalDate): Unit
}
