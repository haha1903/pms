package com.datayes.invest.pms.web.service

import org.joda.time.LocalDate
import javax.inject.Inject
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.security.EquityDao
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.SecurityPositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.PositionValuationHistDao
import com.datayes.invest.pms.dao.security.PriceVolumeDao
import com.datayes.invest.pms.dao.account.CarryingValueHistDao
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.web.model.models.Asset
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.service.industry.IndustryService

class AssetsLoader extends Logging {

  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null

  @Inject
  private var equityDao: EquityDao = null
  
  @Inject
  private var industryService: IndustryService = null
  
  @Inject
  private var marketDataService: MarketDataService = null

  @Inject
  private var positionDao: PositionDao = null
  
  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var positionValuationHistDao: PositionValuationHistDao = null

  @Inject
  private var priceVolumeDao: PriceVolumeDao = null
  
  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityPositionDao: SecurityPositionDao = null


  def loadAssets(accountId: Long, asOfDate: LocalDate): Seq[Asset] = {
    logger.debug("Loading assets from account #{} on as of date {}", accountId, asOfDate)
    val positions = securityPositionDao.findByAccountId(accountId)
    if (positions == null) {
      throw new RuntimeException("Code error: this should never happen.")
    }

    val positionHistMap = loadPositionHists(positions, asOfDate)
    val carryingValueHistMap = loadCarryingValueHists(positions, asOfDate)
    val positionValuationHistMap = loadPositionValuationHists(positions, asOfDate)
    val prevPositionValuationHistMap = loadPositionValuationHists(positions, asOfDate.minusDays(1))

    val assets = positions.flatMap { p =>

      val positionId = p.getId.toLong

      val singleLoader = new SingleAssetLoader(
        position = p,
        positionHistOpt = positionHistMap.get(positionId),
        carryingValueHistOpt = carryingValueHistMap.get(positionId),
        positionValuationHistOpt = positionValuationHistMap.get(positionId),
        previousPositionValuationHistOpt = prevPositionValuationHistMap.get(positionId),
        asOfDate = asOfDate,
        marketDataService = marketDataService,
        industryService = industryService,
        securityDao = securityDao
      )

      singleLoader.load()
    }
    // fill in accountId
    assets.foreach(_.accountId = accountId)
    val nonEmptyAssets = assets.filter { a => a.holdingQuantity > 0 }
    nonEmptyAssets
  }

  private def loadCarryingValueHists(positions: Seq[Position], asOfDate: LocalDate): Map[Long, CarryingValueHist] = {
    val ids = positions.map(_.getId)
    val hists = carryingValueHistDao.findByPositionIdListTypeIdAsOfDate(ids, DefaultValues.CARRYING_VALUE_TYPE, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def loadPositionHists(positions: Seq[Position], asOfDate: LocalDate): Map[Long, PositionHist] = {
    val ids = positions.map(_.getId)
    val hists = positionHistDao.findByPositionIdListAsOfDate(ids, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def loadPositionValuationHists(positions: Seq[Position], asOfDate: LocalDate):
      Map[Long, PositionValuationHist] = {

    val ids = positions.map(_.getId)
    val hists = positionValuationHistDao.findByPositionIdListTypeIdAsOfDate(ids, DefaultValues.POSITION_VALUATION_TYPE.getDbValue, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }
}
