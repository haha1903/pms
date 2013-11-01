package com.datayes.invest.pms.logic.valuation.position

import com.datayes.invest.pms.entity.account.Account
import org.joda.time.LocalDate

trait PositionValuationLogic {

  def process(account: Account, asOfData: LocalDate): Unit
}