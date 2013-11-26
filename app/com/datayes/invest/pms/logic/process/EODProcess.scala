package com.datayes.invest.pms.logic.process

import org.joda.time.LocalDate
import javax.inject.Inject
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.process.eod.ForceLiquidationProcessor
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.logic.valuation.position.PositionValuationLogicFactory
import com.datayes.invest.pms.logic.valuation.account.AccountValuationLogicFactory
import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.logic.valuation.ValuationFacade

class EODProcess extends Logging {
  
  @Inject
  private var forceLiquidationProcessor: ForceLiquidationProcessor = null

  @Inject
  private var valuationFacade: ValuationFacade = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    forceLiquidationProcessor.process(account, asOfDate)
    valuationFacade.valuate(account, asOfDate)
  }

}