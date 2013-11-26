package com.datayes.invest.pms.logic.valuation

import org.joda.time.LocalDate

import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.positionyield.impl.PositionYieldLogicImpl
import com.datayes.invest.pms.logic.valuation.account.AccountValuationLogicFactory
import com.datayes.invest.pms.logic.valuation.position.PositionValuationLogicFactory

import javax.inject.Inject

class ValuationFacade extends Logging {

  @Inject
  private var accountValuationLogicFactory: AccountValuationLogicFactory = null
  
  @Inject
  private var positionValuationLogicFactory: PositionValuationLogicFactory = null
  
  @Inject
  private var positionYieldLogic: PositionYieldLogicImpl = null
  
  def valuate(account: Account, asOfDate: LocalDate): Unit = ValuationLock.withLocking {
    for (t <- PositionValuationType.values()) {
      val logic = positionValuationLogicFactory.get(t)
      logic.process(account, asOfDate);
    }
    {
      // Account valuation
      val logic = accountValuationLogicFactory.get()
      logic.process(account, asOfDate)
      
      // Position yield calculation
      positionYieldLogic.process(account, asOfDate)
    }
  }
}
