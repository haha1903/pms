package com.datayes.invest.pms.web.service

import javax.inject.Inject
import org.joda.time.{LocalDateTime, LocalDate}
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.BigDecimal._
import util.control.Breaks._
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.AccountValuationHistDao
import com.datayes.invest.pms.dao.security.EquityDao
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.dao.security.PriceVolumeDao
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.web.model.models._
import com.datayes.invest.pms.dbtype.AccountValuationType
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.dbtype.AssetClass
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.entity.account.AccountValuationHist


class DashboardService extends Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var assetsLoader: AssetsLoader = null

  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null

  @Inject
  private var equityDao: EquityDao = null

  @Inject
  private var marketDataService: MarketDataService = null

  @Inject 
  private var priceVolumeDao: PriceVolumeDao = null
  
  @Inject
  private var securityDao: SecurityDao = null


  def getAccountList(): Seq[Account] = {
    val accounts = accountDao.findEffectiveAccounts(LocalDate.now)
    accounts.map { a =>
      Account(
        id = a.getId,
        name = Option(a.getAccountName),
        accountNo = Option(a.getAccountNo),
        countryCode = a.getCountryCode,
        currencyCode = a.getCurrencyCode,
        classCode = a.getAccountClass,
        openDate = Option(a.getOpenDate).map(p => LocalDateTime.fromDateFields(p.toDate))
      )
    }
  }

  def getAssetClassWeight(accountId: Long, asOfDate: LocalDate): Seq[AssetClassWeight] = {
    val securityAssets = assetsLoader.loadAssets(accountId, asOfDate)
    val cashAsset = loadCashAsset(accountId, asOfDate)
    val allAssets = cashAsset :: securityAssets.toList
    val assetClassWeights = allAssets.groupBy(a => a.assetClass).map { case (assetClass, assets) =>
      var obj = AssetClassWeight(assetClass)
      for (a <- assets) {
        obj.marketValue += a.marketValue
        obj.floatPnL += a.floatPnL
      }
      obj
    }.toSeq

    var totalMarketValue = BigDecimal("0")
    var totalFloatPnL = BigDecimal("0")
    for (w <- assetClassWeights) {
      totalMarketValue += w.marketValue
      totalFloatPnL += w.floatPnL
      if (w.marketValue > 0) w.floatPnLRate = w.floatPnL / w.marketValue
    }

    if (totalMarketValue > 0) {
      for (w <- assetClassWeights)
        w.weight = w.marketValue / totalMarketValue
    }

    val allAssetClasses = AssetClassType.values.toSeq.filter(_ != AssetClassType.none)
    val assetClassWeights2 = for {
      ac <- allAssetClasses
      acwOpt = assetClassWeights.find(_.assetClass == ac)
    } yield {
      acwOpt match {
        case Some(acw) => acw
        case None => AssetClassWeight(assetClass = ac)
      }
    }

    val sorted = assetClassWeights2.sortBy { acw => acw.weight * -1 }

    sorted
  }

  private def loadCashAsset(accountId: Long, asOfDate: LocalDate): Asset = {
    val pk = new AccountValuationHist.PK(accountId, AccountValuationType.CASH.getDbValue, asOfDate)
    val valHist = accountValuationHistDao.findById(pk)
    val value: BigDecimal = if (valHist != null) {
      valHist.getValueAmount
    } else {
      BigDecimal(0)
    }
    val asset = Asset(name = "Cash", code = "Cash", securityId = 0L)
    asset.assetClass = AssetClassType.cash
    asset.marketValue = value
    asset
  }

  def getIndustryWeight(accountId: Long, asOfDate: LocalDate): IndustryWeightTree = {
    val allAssets = assetsLoader.loadAssets(accountId, asOfDate)
    val industryWeights = allAssets.groupBy(a => a.industry).map { case (industry, assets) =>
      var obj = IndustryWeightLeaf(industry)
      for (a <- assets) {
        obj.marketValue += a.marketValue
      }
      obj
    }.toSeq

    var totalMarketValue = BigDecimal("0")
    for (w <- industryWeights) {
      totalMarketValue += w.marketValue
    }
    if (totalMarketValue > 0) {
      for (w <- industryWeights)
        w.weight = w.marketValue / totalMarketValue
    }
    val sortedIndustryWeights = industryWeights.sortBy { w => w.weight * -1 }
    // TODO refactor with translation resource
    val tree = IndustryWeightTree("所有行业", sortedIndustryWeights)
    tree.marketValue = totalMarketValue
    tree.weight = 1

    tree
  }
  
  def gettopHoldingStock(accountId: Long, number: Integer, asOfDate: LocalDate): TopHoldingStock = {
    val allAssets = assetsLoader.loadAssets(accountId, asOfDate).filter(p => filterFuture(p))
    var totalMarketValue = allAssets.foldLeft(BigDecimal(0))(_ + _.marketValue)
    var topAssetList = allAssets.sortWith(_.marketValue > _.marketValue).take(number)
    var topHoldingMarketValue = topAssetList.foldLeft(BigDecimal(0))(_ + _.marketValue)
    val holdings = topAssetList.map(p => convertToHoldings(p, totalMarketValue)).toSeq
    var weight: BigDecimal = 0
    if (totalMarketValue != 0) {
      weight = topHoldingMarketValue / totalMarketValue
    }
    val topHoldingStock = TopHoldingStock(number, weight, holdings)
    topHoldingStock
  }
  
  private def filterFuture(asset: Asset): Boolean = {
    val security = securityDao.findById(asset.securityId)
    security.getAssetClassId != AssetClass.FUTURE.getDbValue
  }
  
  private def convertToHoldings(asset: Asset, marketValue: BigDecimal): Holding = {
    var holding = Holding(asset.name, asset.code)
    holding.marketPrice = asset.marketPrice
    holding.marketValue = asset.marketValue
    holding.holdingValuePrice = asset.holdingValuePrice
    holding.industry = asset.industry
    holding.floatPnL = asset.floatPnL
    if (marketValue != 0) {
      holding.weight = asset.marketValue / marketValue
    }
    holding
  }
  
  def getPerformance(accountId: Long, asOfDate: LocalDate, standardFund: List[(String, Long)]): Seq[Performance] = {
    var performance = standardFund.map(p => getFundPerformance(p, accountId, asOfDate)).toSeq
    performance
  }
  
  private def getFundPerformance(fundNameAndId: (String, Long), accountId: Long, asOfDate: LocalDate): Performance = {
    val weekFunc = (d: LocalDate) => d.minusDays(7)
    val monthFunc = (d: LocalDate) => d.minusMonths(1)
    val quaterFunc = (d: LocalDate) => d.minusMonths(3)
    val halfYearFunc = (d: LocalDate) => d.minusMonths(6)
    val yearFunc = (d: LocalDate) => d.minusYears(1)
    val yearToDateFunc = (d: LocalDate) => new LocalDate(d.getYear(), 1, 1)
    val timeFunc = List(weekFunc, monthFunc, quaterFunc, halfYearFunc, yearFunc, yearToDateFunc)

    getStandardFundPerformance(fundNameAndId, asOfDate, timeFunc)
  }
  
  private def getStandardFundPerformance(fundNameAndId: (String, Long), asOfDate: LocalDate,
      timeFunc:List[LocalDate => LocalDate]): Performance =  {

    var changes: List[BigDecimal] = null
    if (fundNameAndId._1 == DefaultValues.THIS_FUND) {
      changes = timeFunc.map( p => getFundReturn(fundNameAndId._2, asOfDate, p))
    } else {
      val securityId = fundNameAndId._2
      val indexValue = getEquityMarketPrice(securityId, asOfDate)
      changes = timeFunc.map( p => getIndex(securityId, asOfDate, p, indexValue.toDouble))
    }
    var performance: Performance = Performance(fundNameAndId._1, 
            changes(DefaultValues.CHANGE_LAST_WEEK),
            changes(DefaultValues.CHANGE_LAST_MONTH),
            changes(DefaultValues.CHANGE_LAST_QUATER), 
            changes(DefaultValues.CHANGE_LAST_HALFYEAR), 
            changes(DefaultValues.CHANGE_LAST_YEAR),
            changes(DefaultValues.CHANGE_YEAR_TO_DATE))
    performance
  }

  // TODO This can be refactored as well
  private def getEquityMarketPrice(securityId: Long, asOfDate: LocalDate): BigDecimal = {
    val md = marketDataService.getMarketData(securityId, asOfDate)
    if (md == null) {
      logger.error("Unable to find market data for security #" + securityId + " on " + asOfDate)
      BigDecimalConstants.ZERO
    } else {
      md.getPrice()
    }
  }
  
  private def getFundReturn(accountId: Long, asOfDate: LocalDate, func: LocalDate => LocalDate): BigDecimal = {
    var date = func(asOfDate)
    var fundReturn: BigDecimal = 1
    while (date.compareTo(asOfDate) <= 0) {
      val pk = new AccountValuationHist.PK(accountId, AccountValuationType.DAILY_RETURN.getDbValue, date)
      var accountValuationHist = accountValuationHistDao.findById(pk)
      if (accountValuationHist != null) {
        val dailyReturn: BigDecimal = accountValuationHist.getValueAmount
        fundReturn = fundReturn * (dailyReturn + 1)
      }
      date = date.plusDays(1)
    }
    fundReturn - 1
  }
  
  private def getIndex(securityId: Long, asOfDate: LocalDate, func: LocalDate => LocalDate, todaysIndex: Double): BigDecimal = {
    val oldDate = func(asOfDate)
    
    val md = marketDataService.getMarketData(securityId, oldDate)
    if (md != null) {
      val price = md.getPrice()
      val change = (BigDecimal(todaysIndex) - price) / price
      change
    } else {
      BigDecimalConstants.ZERO
    }
  }


  def getNetValueTrends(accountId: Long,
                        beforeDate: LocalDate,
                        benchmarkIndexTicker: String): Seq[NetValueTrendItem] = {

    val netValueHists = loadNetValueHistsBeforeDate(accountId, beforeDate)
    val fundReturnHists = loadFundReturnHistsBeforeDate(accountId, beforeDate)
    val startDateOpt = fundReturnHists.headOption.map(_._1)

    val items = startDateOpt match {
      case Some(startDate) =>
        val equityList =  equityDao.findByTickerSymbol(benchmarkIndexTicker)
        val benchmarkSecurityId = {
          if(null == equityList || equityList.isEmpty) {
            throw new RuntimeException("Can not find Security Id for Ticker Symbol: " +
              benchmarkIndexTicker + " in EQUITY")
          }
          else {
            //If there are several securities for XSHG, choose the first one
            var securityId = equityList.get(0).getId
            for(e <- equityList) {
              breakable {
                if(DefaultValues.SH_STOCK_EXCHANGE_CODE == e.getExchangeCode) {
                  securityId = e.getId
                  break
                }
              }
            }
            securityId
          }
        }

        val benchmarkReturns = loadBenchmarkReturns(benchmarkSecurityId, startDate, beforeDate)
        val merged = mergeToNetValueTrendItems(netValueHists, fundReturnHists, benchmarkReturns)
        merged
      case None =>
        Seq.empty[NetValueTrendItem]
    }
    logger.debug(items.size + " net value trend items loaded for accountId #" +
      accountId + " before " + beforeDate)
    items
  }

  private def mergeToNetValueTrendItems(netValueHists: Seq[(LocalDate, BigDecimal)],
      fundReturnsHists: Seq[(LocalDate, BigDecimal)], benchmarkReturns: Seq[(LocalDate, BigDecimal)]):
      Seq[NetValueTrendItem] = {

    // Find a later start date
    val startDateOpt: Option[LocalDate] = for {
      date1 <- fundReturnsHists.headOption.map(_._1)
      date2 <- benchmarkReturns.headOption.map(_._1)
    } yield if (date1.compareTo(date2) > 0) date1 else date2

    val items = startDateOpt match {
      case Some(startDate) =>
        val newNetValueHists = netValueHists.dropWhile { case (date, value) => date.compareTo(startDate) < 0 }
        val newFundReturns = fundReturnsHists.dropWhile { case (date, value) => date.compareTo(startDate) < 0 }
        val newBenchmarkReturns = benchmarkReturns.dropWhile { case (date, value) => date.compareTo(startDate) < 0 }

        newNetValueHists.zip(newFundReturns).zip(newBenchmarkReturns).map { case ((nv, fr), br) =>
          val (date, netValue, fundReturn, benchmarkReturn) = (nv._1, nv._2, fr._2, br._2)
          if (date.compareTo(fr._1) != 0 || date.compareTo(br._1) != 0) {
            throw new RuntimeException("Code error, dates should be the same for returns")
          }
          NetValueTrendItem(date, netValue, fundReturn, benchmarkReturn)
        }

      case None =>
        Seq.empty[NetValueTrendItem]
    }
    items
  }

  private def loadBenchmarkReturns(securityId: Long, startDate: LocalDate, endDate: LocalDate):
      Seq[(LocalDate, BigDecimal)] = {

    val priceVolumes = priceVolumeDao.findSomeBySecurityIdInPeriod(securityId, startDate, endDate)

    val rets = priceVolumes.headOption match {
      case Some(firstPriceVolume) =>
        priceVolumes.map { p =>
          val ratio = BigDecimal(p.getPriceClose / firstPriceVolume.getPriceClose - 1)
          val date = p.getTradeDate
          (date, ratio)
        }
      case None =>
        Seq.empty[(LocalDate, BigDecimal)]
    }
    logger.debug("{} benchmark returns loaded for benchmark security #{} between {} and {}", rets.size, securityId, startDate, endDate)

    smoothifyDates(rets)
  }

  private def smoothifyDates(dateValues: Seq[(LocalDate, BigDecimal)]): Seq[(LocalDate, BigDecimal)] = {
    if (dateValues == null || dateValues.isEmpty) {
      Seq.empty[(LocalDate, BigDecimal)]

    } else {
      val sorted = dateValues.sortWith { case (p1, p2) => p1._1.compareTo(p2._1) <= 0 }
      var prevOpt: Option[(LocalDate, BigDecimal)] = None
      val buf = mutable.ListBuffer.empty[(LocalDate, BigDecimal)]

      buf.append(sorted.head)
      var prev = sorted.head
      for ((date, value) <- sorted.tail) {
        val (prevDate, prevValue) = prev
        if (date.minusDays(1).compareTo(prevDate) > 0) {
          var d = prevDate.plusDays(1)
          while (d.compareTo(date) < 0) {
            buf.append((d, prevValue))
            d = d.plusDays(1)
          }
        }

        buf.append((date, value))
        prev = (date, value)
      }
      
      buf
//
//      val sorted = buf.sortWith { (a, b) => a._1.compareTo(b._1) <= 0 }
//      sorted
    }
  }

  private def getEarlierDateOption(date1: Option[LocalDate], date2: Option[LocalDate]): Option[LocalDate] = {
    (date1, date2) match {
      case (Some(d1), Some(d2)) => if (d1.compareTo(d2) < 0) Some(d1) else Some(d2)
      case (Some(d1), None) => Some(d1)
      case (None, Some(d2)) => Some(d2)
      case (None, None) => None
    }
  }

  private def loadNetValueHistsBeforeDate(accountId: Long, sinceDate: LocalDate):
      Seq[(LocalDate, BigDecimal)] = {

    val accValType = AccountValuationType.NET_WORTH
    val accValHists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId,
      accValType.getDbValue, sinceDate)
    val hists = accValHists.map {
      h => (h.getAsOfDate, h.getValueAmount)
    }
    logger.debug("{} net value hists loaded for account #{} before date {}, earliest hist: {}",
      hists.size, accountId, sinceDate, hists.headOption)

    hists
  }

  private def loadFundReturnHistsBeforeDate(accountId: Long, beforeDate: LocalDate):
      Seq[(LocalDate, BigDecimal)] = {

    val accValType = AccountValuationType.DAILY_RETURN
    val accValHists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId,
      accValType.getDbValue, beforeDate)
    var valueAccu = BigDecimal("1")
    val fundReturnHists = accValHists.map { h =>
      if (h != null && h.getValueAmount != null) {
        valueAccu = valueAccu * (h.getValueAmount + 1)
      }
      (h.getAsOfDate, valueAccu - 1)
    }
    logger.debug("{} net value hists loaded for account #{} before date {}, earliest hist: {}",
      fundReturnHists.size, accountId, beforeDate, fundReturnHists.headOption)
    fundReturnHists
  }

  def getAccountOverview(accountId: Long, asOfDate: LocalDate): AccountOverview = {
    val unitNet = getOverviewValue(accountId, asOfDate, AccountValuationType.UNIT_NET)
    val dailyReturn = getOverviewValue(accountId, asOfDate, AccountValuationType.DAILY_RETURN)
    val marketValue = getOverviewValue(accountId, asOfDate, AccountValuationType.SECURITY)  // TODO this may not be correct
    val cashValue = getOverviewValue(accountId, asOfDate, AccountValuationType.CASH)
    val fundReturn = getFundReturnAsOfDate(accountId, asOfDate)
    val pnl = getOverviewValue(accountId, asOfDate, AccountValuationType.PROFIT_LOSS)

    AccountOverview(
      unitNet,
      dailyReturn,
      marketValue,
      cashValue,
      fundReturn,
      pnl
    )
  }

  private def getFundReturnAsOfDate(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val accValType = AccountValuationType.DAILY_RETURN
    val accValHists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId,
      accValType.getDbValue, asOfDate)
    val ratio = accValHists.foldLeft(BigDecimal("1")) { (product, valHist) =>
      product * (valHist.getValueAmount + 1)
    }
    ratio - 1
  }

  private def getOverviewValue(accountId: Long, asOfDate: LocalDate,
      accValType: AccountValuationType): BigDecimal = {

    val pk = new AccountValuationHist.PK(accountId, accValType.getDbValue, asOfDate)
    val valHist = accountValuationHistDao.findById(pk)
    if (valHist == null) {
      logger.warn("Failed to load account valuation hist ({}) for account #{} on {}", accValType, accountId, asOfDate)
      BigDecimal("0")
    } else if (valHist.getValueAmount == null) {
      logger.warn("Account valuation hist ({}) for account #{} on {} is null", accValType, accountId, asOfDate)
      BigDecimal("0")
    }else {
      valHist.getValueAmount
    }
  }
}
