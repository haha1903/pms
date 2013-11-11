package com.datayes.invest.pms.logic.valuation.position.impl

import javax.inject.Inject
import scala.collection.JavaConversions._
import org.joda.time.LocalDate
import scala.BigDecimal._
import scala.collection.mutable
import java.lang.{ Long => JLong }
import java.util.{ List => JList, ArrayList, Map => JMap }
import java.util.HashSet
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.logic.valuation.position.PositionValuationLogic
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.SecurityPositionDao
import com.datayes.invest.pms.dao.account.CashPositionDao
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.PositionValuationHistDao
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.dao.security.FutureDao
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.entity.account._
import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.security.Equity
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.entity.security.Repo
import scala.Some


class MarketValuationLogic extends PositionValuationLogic with Logging {

  private val valuationType = PositionValuationType.MARKET

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var securityPositionDao: SecurityPositionDao = null

  @Inject
  private var cashPositionDao: CashPositionDao = null

  @Inject
  private var positionDao: PositionDao = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var positionValuationHistDao: PositionValuationHistDao = null

  @Inject
  private var securityDao: SecurityDao = null
  
  @Inject
  private var marketDataService: MarketDataService = null

  private var asOfDate: LocalDate = null
  
  private var equityMarketDataMap: JMap[JLong, MarketData] = null

  override def process(account: Account, asOfDate: LocalDate): Unit = {
    this.asOfDate = asOfDate
    val positions = positionDao.findByAccountId(account.getId)
    preloadMarketData(positions, asOfDate)
    
    logger.debug("Valuating account #{} with {} security positions on {}",
      account.getId, positions.size, asOfDate)

    val positionHistMap = loadPositionHists(positions)
    val positionValuationHistMap = loadPositionValuationHists(positions)

    for (p <- positions) {
      val positionId = p.getId.toLong
      val valHistOpt = positionValuationHistMap.get(positionId)
      positionHistMap.get(positionId) match {
        case Some(hist) => valuatePosition(p, hist, valHistOpt)
        case None =>
//          logger.info("Failed to find position history for position #{} on {}", positionId, asOfDate)
      }
    }
  }
  
  private def preloadMarketData(positions: java.util.List[Position], asOfDate: LocalDate): Unit = {
    logger.debug("Preloading equity market data for market valuation on {}", asOfDate)
    val securityIds = new HashSet[JLong]
    for (p <- positions) {
      p match {
        case sp : SecurityPosition =>
          securityIds.add(sp.getSecurityId())
        case _ =>
          // ignore cash positions
      }
    }
    // TODO handle future price
    equityMarketDataMap = marketDataService.getMarketData(securityIds, asOfDate)
  }

  private def loadPositionHists(positions: Seq[Position]): Map[Long, PositionHist] = {
    val ids = positions.map(_.getId)
    val hists = positionHistDao.findByPositionIdListAsOfDate(ids, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def loadPositionValuationHists(positions: Seq[Position]): Map[Long, PositionValuationHist] = {
    val ids = positions.map(_.getId)
    val hists = positionValuationHistDao.findByPositionIdListTypeIdAsOfDate(ids, valuationType.getDbValue, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def getEquityMarketPrice(securityId: Long): BigDecimal = {
    val md = equityMarketDataMap.get(securityId)
    if (md == null) {
      logger.error("Unable to find market data for security #{} on {}", securityId, asOfDate)
      BigDecimalConstants.ZERO
    } else {
      md.getPrice()
    }
  }

  private def getFutureMarketPrice(securityId: Long): BigDecimal = {
    val md = marketDataService.getMarketData(securityId, this.asOfDate)
    if (md == null) {
      logger.error("Unable to find market data for security #{} on {}", securityId, asOfDate)
      BigDecimalConstants.ZERO
    } else {
      md.getPrice()
    }
  }

  private def valuatePosition(position: Position, positionHist: PositionHist, positionValuationHistOpt: Option[PositionValuationHist]): Unit = {
    val (price: BigDecimal, currencyCode: String) = position match {
      case securityPosition: SecurityPosition =>
        val security = securityDao.findById(securityPosition.getSecurityId)
        security match {
          case equity: Equity =>
            val p = getEquityMarketPrice(securityPosition.getSecurityId)
            val c = securityPosition.getCurrencyCode
            (p, c)

          case future: Future =>
            logger.debug("Future #{}", securityPosition.getSecurityId)
            // TODO How to get STOCK_INDEX_FUTURE_PRICE_RATIO?
            val p = getFutureMarketPrice(securityPosition.getSecurityId) * DefaultValues.STOCK_INDEX_FUTURE_PRICE_RATIO
            val c = securityPosition.getCurrencyCode
            (p, c)

          case repo: Repo => (BigDecimal(0), securityPosition.getCurrencyCode)            
          
          case x =>
            throw new RuntimeException("Unable to handle security " + x.getClass)
        }

      case cashPosition: CashPosition =>
        (DefaultValues.CASH_PRICE, cashPosition.getCurrencyCode)

      case s => throw new RuntimeException("Unable to handle position " + (if (s == null) "null" else s.getClass.getName))
    }

    logger.debug("Position #{} quantity: {} on {}", positionHist.getPK.getPositionId, positionHist.getQuantity, asOfDate)
    val valueAmount = price * positionHist.getQuantity

    savePositionValuationHist(position, positionHist, positionValuationHistOpt, price, valueAmount, currencyCode)
  }

  private def savePositionValuationHist(position: Position, positionHist: PositionHist,
    positionValuationHistOpt: Option[PositionValuationHist],
    marketPrice: BigDecimal, valueAmount: BigDecimal, currencyCode: String): Unit = {

    positionValuationHistOpt match {
      case Some(hist) =>
        hist.setValueAmount(valueAmount.bigDecimal)
        hist.setMarketPrice(marketPrice.bigDecimal)
        hist.setAdjustTs(positionHist.getLastUpdate) // TODO should not be null
        positionValuationHistDao.update(hist)

      case None =>
        val pk = new PositionValuationHist.PK(position.getId, valuationType.getDbValue, asOfDate)
        val newHist = new PositionValuationHist(pk, currencyCode)
        newHist.setValueAmount(valueAmount.bigDecimal)
        newHist.setMarketPrice(marketPrice.bigDecimal)
        newHist.setAdjustTs(positionHist.getLastUpdate) // TODO positionHist.lastUpdate should be not null
        positionValuationHistDao.save(newHist);
    }
  }
}