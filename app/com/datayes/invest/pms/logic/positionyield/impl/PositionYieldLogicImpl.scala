package com.datayes.invest.pms.logic.positionyield.impl

import com.datayes.invest.pms.dao.account.{CashPositionDao, SecurityPositionDao, PositionDao}
import com.datayes.invest.pms.logging.Logging
import org.joda.time.LocalDate
import com.datayes.invest.pms.entity.account.{Position, Account, SecurityPosition, CashPosition}
import javax.inject.Inject
import com.datayes.invest.pms.logic.positionyield.{PositionYieldCalcFactory, PositionYieldLogic}
import scala.collection.JavaConversions._
import com.datayes.invest.pms.dbtype.LedgerType


class PositionYieldLogicImpl extends PositionYieldLogic with Logging {
  @Inject
  private var cashPositionDao: CashPositionDao = null

  @Inject
  private var securityPositionDao: SecurityPositionDao = null

  @Inject
  private var positionYieldCalcFactory: PositionYieldCalcFactory = null

  override def process(account: Account, asOfDate: LocalDate): Unit = {
    val accountId = account.getId
    val cashPositions = cashPositionDao.findByAccountId(accountId).filter(position => LedgerType.CASH.getDbValue == position.getLedgerId ).toList
    val secPositions = securityPositionDao.findByAccountId(accountId).toList
    val stockPositions = secPositions.filter(position => LedgerType.SECURITY.getDbValue == position.getLedgerId)
    val futurePositions = secPositions.filter(position => LedgerType.FUTURE_LONG.getDbValue == position.getLedgerId || LedgerType.FUTURE_SHORT.getDbValue == position.getLedgerId)


    doProcess(cashPositions, asOfDate, LedgerType.CASH)
    doProcess(stockPositions, asOfDate, LedgerType.SECURITY)
    doProcess(futurePositions, asOfDate, LedgerType.FUTURE_LONG)
  }

  private def doProcess(positions: List[Position], asOfDate: LocalDate, ledgerType: LedgerType): Unit = {
    val positionYieldCalc = positionYieldCalcFactory.get(ledgerType)
    positionYieldCalc.process(positions, asOfDate)
  }

}
