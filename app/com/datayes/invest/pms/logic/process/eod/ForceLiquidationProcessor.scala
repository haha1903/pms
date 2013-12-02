package com.datayes.invest.pms.logic.process.eod

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.{ SecurityPositionDao, PositionHistDao }
import com.datayes.invest.pms.dao.security.{ SecurityDao, FutureDao }
import com.datayes.invest.pms.entity.account.{Account, SecurityPosition}
import com.datayes.invest.pms.entity.security.{ Equity, Future }
import com.datayes.invest.pms.util.DefaultValues
import scala.math.BigDecimal.int2bigDecimal
import org.joda.time.{LocalTime, LocalDate}
import javax.inject.Inject
import scala.collection.JavaConversions._
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.logic.transaction.TransactionLogicFactory
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.logic.process.Processor


class ForceLiquidationProcessor extends Processor with Logging {

  @Inject
  private var futureDao: FutureDao = null
  
  @Inject
  private var marketDataService: MarketDataService = null
  
  @Inject
  private var positionHistDao: PositionHistDao = null
  
  @Inject
  private var securityDao: SecurityDao = null
  
  @Inject
  private var securityPositionDao: SecurityPositionDao = null
  
  @Inject
  private var transactionLogicFactory: TransactionLogicFactory = null
  
  
  def process(account: Account, asOfDate: LocalDate): Unit = {
    logger.debug("Force liquidation started for account #{}", account.getId)
    forceLiquidation(account.getId, asOfDate)
    logger.info("Force liquidation finished for account #{}", account.getId)
  }
  
  private def forceLiquidation(accountId: Long, asOfDate: LocalDate): Unit = {
    val positionList = securityPositionDao.findByAccountId(accountId).
      filter(p => filterFuturesOnClosingDay(p, asOfDate))
    for (position <- positionList) {
      doTransaction(accountId, position, asOfDate)
    }
  }
  
  private def filterFuturesOnClosingDay(securityPosition: SecurityPosition, asOfDate: LocalDate): Boolean = {
    val security = securityDao.findById(securityPosition.getSecurityId)
    security match {
      case future: Future =>
        if (future.getDeliveryDate == null) {
          false
        } else {
          asOfDate.isEqual(future.getDeliveryDate.toLocalDate())
        }
      case _ => false
    }
  }
  
  private def doTransaction(accountId: Long, securityPosition: SecurityPosition, asOfDate: LocalDate): Unit = {
    val quantity = getQuantity(securityPosition.getId, asOfDate)
    val price = getFutureMarketPrice(securityPosition.getSecurityId, asOfDate)
    if (quantity == 0) {
      return
    }
    logger.debug("start force liquidation on account #{} positionId = {}, quantity = {}, price = {}", 
            accountId, securityPosition.getId, quantity, price)
    val tradeSide = getTradeSide(securityPosition.getLedgerId)
    val t = Transaction(accountId, securityPosition.getSecurityId,
            DefaultValues.PMS_SOURCE_TRANSACTION_ID, getLongOption(null), getLongOption(null), asOfDate.toLocalDateTime(LocalTime.MIDNIGHT),
            null, tradeSide, price, quantity, TransactionSource.PMS.getDbValue, TransactionClass.TRADE)
    val engine = transactionLogicFactory.get(t)
    engine.process(t)
  }
  
  private def getQuantity(positionId: Long, asOfDate: LocalDate): BigDecimal = {
    val positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)
    if (positionHist != null) {
      positionHist.getQuantity
    } else {
      throw new RuntimeException("failed to get positionHist on position#" + positionId)
      0
    }
  }
  
  private def getFutureMarketPrice(securityId: Long, asOfDate: LocalDate): BigDecimal = {
    val md = marketDataService.getMarketData(securityId, asOfDate)
    if (md == null || md.getPrice() == null) {
      logger.error("Unable to find market data for security #{} on {}", securityId, asOfDate)
      BigDecimalConstants.ZERO
    } else {
      md.getPrice()
    }
  }
  
  private def getTradeSide(ledgerId: Long): TradeSide = {
    if (ledgerId == LedgerType.FUTURE_LONG.getDbValue) {
      TradeSide.SELL
    } else if (ledgerId == LedgerType.FUTURE_SHORT.getDbValue) {
      TradeSide.COVER
    } else {
      throw new RuntimeException("failed to get trade side on ledger#" + ledgerId)
    }
  }
  
  private  def getLongOption(d: java.lang.Long): Option[Long] = {
    if (d == null) {
      None
    } else {
      Some(d)
    }
  }
}