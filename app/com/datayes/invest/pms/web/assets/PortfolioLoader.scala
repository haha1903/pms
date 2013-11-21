package com.datayes.invest.pms.web.assets

import scala.collection.JavaConversions._
import scala.collection.mutable
import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.CarryingValueHistDao
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.PositionValuationHistDao
import com.datayes.invest.pms.dao.security.EquityDao
import com.datayes.invest.pms.dao.security.PriceVolumeDao
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.entity.account.CashPosition
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.security.Equity
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.industry.IndustryService
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.service.marketindex.MarketIndexService
import com.datayes.invest.pms.util.DefaultValues
import javax.inject.Inject
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.dao.account.AccountValuationHistDao
import com.datayes.invest.pms.dbtype.AccountValuationType
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.entity.account.AccountValuationHist

class PortfolioLoader extends Logging {

  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null
  
  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null

  @Inject
  private var equityDao: EquityDao = null
  
  @Inject
  private var industryService: IndustryService = null
  
  @Inject
  private var marketDataService: MarketDataService = null
  
  @Inject
  private var marketIndexService: MarketIndexService = null

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
  
  
  def load(accounts: Seq[Account], asOfDate: LocalDate, benchmarkIndexOpt: Option[String]): Seq[models.AssetCommon] = {
    val allAssets = accounts.flatMap { acc =>
      val assets = internalLoad(acc.getId, asOfDate, benchmarkIndexOpt)
      assets
    }
    var netValue = BigDecimalConstants.ZERO
    for (acc <- accounts) {
      val v = getNetValue(acc, asOfDate)
      if (v != null) {
        netValue = netValue + v
      }
    }
    calculateWeight(allAssets, netValue)
    
    allAssets
  }
  
  def load(account: Account, asOfDate: LocalDate, benchmarkIndexOpt: Option[String]): Seq[models.AssetCommon] = {
    val assets = internalLoad(account.getId, asOfDate, benchmarkIndexOpt)
    val netValue = getNetValue(account, asOfDate)
    calculateWeight(assets, netValue)
    
    assets
  }
  
  private def calculateWeight(assets: Seq[models.AssetCommon], netValue: BigDecimal): Unit = {
    if (netValue != null && netValue.abs > BigDecimalConstants.EPSILON) {
      for (a <- assets) {
        a.weight = a.marketValue / netValue
      }
    }
  }
  
  private def getNetValue(account: Account, asOfDate: LocalDate): BigDecimal = {
    val pk = new AccountValuationHist.PK(account.getId, AccountValuationType.NET_WORTH.getDbValue(), asOfDate)
    val hist = accountValuationHistDao.findById(pk)
    if (hist == null) {
      logger.warn("Net value not found for account {}", account.getId)
      null
    } else if (hist.getValueAmount() == null || hist.getValueAmount().abs < BigDecimalConstants.EPSILON) {
      logger.warn("Net value is or close to zero for account {}: {}", account.getId, hist.getValueAmount())
      null
    } else {
      hist.getValueAmount()
    }
  }

  private def internalLoad(accountId: Long, asOfDate: LocalDate, benchmarkIndexOpt: Option[String]): Seq[models.AssetCommon] = {
    logger.debug("Loading portfolio from account #{} on as of date {}", accountId, asOfDate)
    val positions = positionDao.findByAccountId(accountId)
    if (positions == null) {
      throw new RuntimeException("Code error: this should never happen.")
    }

    val positionHistMap = loadPositionHists(positions, asOfDate)
    val carryingValueHistMap = loadCarryingValueHists(positions, asOfDate)
    val positionValuationHistMap = loadPositionValuationHists(positions, asOfDate)
    val prevPositionValuationHistMap = loadPositionValuationHists(positions, asOfDate.minusDays(1))
    
    val assetList = mutable.ListBuffer.empty[models.AssetCommon]
    for (p <- positions) {
      
      val positionId = p.getId.toLong
      val transferObject = new TransferObject(
        positionHist = positionHistMap.get(positionId).getOrElse(null),
        carryingValueHist = carryingValueHistMap.get(positionId).getOrElse(null),
        positionValuationHist = positionValuationHistMap.get(positionId).getOrElse(null),
        prevPositionValuationHist = prevPositionValuationHistMap.get(positionId).getOrElse(null),
        benchmarkIndexOpt = benchmarkIndexOpt,
        marketDataService = marketDataService,
        industryService = industryService,
        marketIndexService = marketIndexService,
        securityDao = securityDao
      )
      
      val assetOpt: Option[models.AssetCommon] = p match {
        case cp: CashPosition =>
          val loader = new CashAssetLoader(cp, asOfDate, transferObject)
          val aOpt = loader.load()
          aOpt
          
        case sp: SecurityPosition =>
          val loader = createAssetLoader(sp, asOfDate, transferObject)
          if (loader != null) {
            val aOpt = loader.load()
            aOpt
          } else {
            None
          }
          
        case x =>
          logger.warn("Unknown position: " + x.getClass())
          None
      }
      
      assetOpt match {
        case Some(asset) if asset.holdingQuantity > 0 =>
          assetList.append(asset)
        case _ =>
      }
    }
    
    assetList
  }
  
  private def createAssetLoader(position: SecurityPosition, asOfDate: LocalDate, tranObj: TransferObject): AssetLoader = {
    val security = securityDao.findById(position.getSecurityId())
    security match {
      case equity: Equity =>
        new EquityAssetLoader(position, asOfDate, tranObj)
      case future: Future =>
        new IndexFutureAssetLoader(position, asOfDate, tranObj)
      case x =>
        logger.warn("Unable to find asset loader for security {}", x)
        null
    }
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
