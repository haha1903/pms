package com.datayes.invest.pms.logic.process

import org.joda.time.LocalDate
import javax.inject.Inject
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.process.eod.ForceLiquidationProcessor
import com.datayes.invest.pms.entity.account.Account

class EODProcess extends Logging {
  
  @Inject
  private var forceLiquidationProcessor: ForceLiquidationProcessor = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    forceLiquidationFuturesOnClosingDay(account, asOfDate)
    doValuation(account, asOfDate)
  }

  private def forceLiquidationFuturesOnClosingDay(account: Account, asOfDate: LocalDate): Unit = {
    forceLiquidationProcessor.process(account, asOfDate)
  }
  
  private def doValuation(account: Account, asOfDate: LocalDate): Unit = {
    // TODO fix it
  }
}