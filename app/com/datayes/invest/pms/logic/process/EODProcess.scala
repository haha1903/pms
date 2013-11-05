package com.datayes.invest.pms.logic.process

import org.joda.time.LocalDate
import javax.inject.Inject
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.process.eod.ForceLiquidationProcessor
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.logic.valuation.position.PositionValuationLogicFactory
import com.datayes.invest.pms.logic.valuation.account.AccountValuationLogicFactory
import com.datayes.invest.pms.dbtype.PositionValuationType

class EODProcess extends Logging {
  
  @Inject
  private var forceLiquidationProcessor: ForceLiquidationProcessor = null
  
  @Inject
  private var positionValuationLogicFactory: PositionValuationLogicFactory = null
  
  @Inject
  private var accountValuationLogicFactory: AccountValuationLogicFactory = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    forceLiquidationFuturesOnClosingDay(account, asOfDate)
    doValuation(account, asOfDate)
  }

  private def forceLiquidationFuturesOnClosingDay(account: Account, asOfDate: LocalDate): Unit = {
    forceLiquidationProcessor.process(account, asOfDate)
  }
  
  private def doValuation(account: Account, asOfDate: LocalDate): Unit = {
    for (pvt <- PositionValuationType.values()) {
      val logic = positionValuationLogicFactory.get(pvt)
      logic.process(account, asOfDate)
    }
    
    val acctLogic = accountValuationLogicFactory.get()
    acctLogic.process(account, asOfDate)
  }
}