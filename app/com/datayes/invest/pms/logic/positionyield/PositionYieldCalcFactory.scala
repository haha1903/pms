package com.datayes.invest.pms.logic.positionyield

import com.datayes.invest.pms.logic.positionyield.impl._
import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Inject
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.positionyield.impl.generic.GenericSecurityYieldCalc
import com.datayes.invest.pms.dbtype.LedgerType


class PositionYieldCalcFactory extends Logging {
  @Inject
  private var cashYieldCalc: CashYieldCalc = null

  @Inject
  private var stockYieldCalc: StockYieldCalc = null

  @Inject
  private var futureYieldCalc: FutureYieldCalc = null

  def get(ledgerType: LedgerType): PositionYieldCalc = ledgerType match {
    case LedgerType.CASH => cashYieldCalc
    case LedgerType.SECURITY => stockYieldCalc
    case LedgerType.FUTURE_LONG | LedgerType.FUTURE_SHORT  => futureYieldCalc
    case _ =>
      logger.error("Cannot find corresponding position type: {}", ledgerType.toString)
      null
  }
}
