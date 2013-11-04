package com.datayes.invest.pms.logic.valuation.account.impl

import javax.inject.Inject
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.mutable
import com.datayes.invest.pms.logic.valuation.account.AccountValuationLogic
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.AccountValuationHistDao
import com.datayes.invest.pms.dao.account.CarryingValueHistDao
import com.datayes.invest.pms.dao.security.FutureDao
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.PositionValuationHistDao
import com.datayes.invest.pms.dao.account.SecurityPositionDao
import com.datayes.invest.pms.dao.account.CashPositionDao
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.dbtype.AccountValuationType
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.account.AccountValuationHist
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.entity.account.CarryingValueHist

class AccountValuationEngineImpl extends AccountValuationLogic with Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null

  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null

  @Inject
  private var futureDao: FutureDao = null

  @Inject
  private var marketDataService: MarketDataService = null

  @Inject
  private var positionDao: PositionDao = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var positionValuationHistDao: PositionValuationHistDao = null

  @Inject
  private var secPositionDao: SecurityPositionDao = null

  @Inject
  private var cashPositionDao: CashPositionDao = null

  private val LATEST_ADJUST_TIME = LocalDateTime.now()

  private var asOfDate: LocalDate = null

  private val adjustTs: mutable.Map[Int, LocalDateTime] = mutable.Map.empty[Int, LocalDateTime]

  private val minDate: LocalDateTime = LocalDateTime.parse("1900-01-01")

  override def process(account: Account, asOfDate: LocalDate): Unit = {
    this.asOfDate = asOfDate
    logger.info("Doing account valuation for as of date {}", asOfDate)
    doProcess(account)
  }

  private def doProcess(account: Account): Unit = {

    logger.debug("start account valuation process")
    initAdjustTsArray()

    val positions = positionDao.findByAccountId(account.getId)
    val positionList = positions.toList

    val security = valuateSecurity(account, positionList)
    val cash = valuateCash(account, positionList)
    val commission = valuateCommission(account, positionList)
    val futurePNL = valuateFuturePNL(account, positionList)
    val futureLongValue = valuateFutureLongValue(account, positionList)
    val futureShortValue = valuateFutureShortValue(account, positionList)
    val futureValue = valuateFutureValue(account, futureLongValue, futureShortValue)
    val margin = valuateMargin(account, positionList, futurePNL)
    val payableSettlement = valuatePayableSettlement(account, positionList)
    val receivableSettlement = valuateReceivableSettlement(account, positionList)
    val (repoPayPrinciple, repoPayInterest, repoReceivePrinciple, repoReceiveInterest) = valulateRepo(account, positionList)
    val asset = valuateAsset(account,
      security,
      cash,
      margin,
      receivableSettlement,
      repoReceivePrinciple + repoReceiveInterest)
    val liability = valuateLiability(account, payableSettlement, commission, repoPayPrinciple + repoPayInterest)
    val netWorth = valuateNetWorth(account, asset, liability)
    val unitNet = valuateUnitNet(account, netWorth)
    val dailyReturn = valuateDailyReturn(account, asOfDate)
    val pnl = valuateProfitAndLoss(account)

    valuateShare(account)
  }

  private def initAdjustTsArray(): Unit = {
    adjustTs.clear()
    for (i <- 1 to AccountValuationType.values().length) {
      adjustTs.put(i, minDate)
    }
  }

  private def findSpecifiedPositions(positions: List[Position], ledgerId: Long): java.util.List[Position] = {
    //    positions.filter(_.getLedgerId == ledgerId)
    val list = new java.util.ArrayList[Position]
    for (p <- positions) {
      if (p.getLedgerId() == ledgerId)
        list.add(p)
    }
    list
  }

  private def findLatestTime(times: Seq[LocalDateTime]): LocalDateTime = {
    times.foldLeft(minDate)((large, date) => if (large.toDate.before(date.toDate)) date else large)
  }

  private def findLatestAdjustTs(): LocalDateTime = {
    //    findLatestTime(adjustTs.unzip._2.toList)
    LATEST_ADJUST_TIME
  }

  private def preciseAsMoney(number: BigDecimal): java.math.BigDecimal = {
    number.bigDecimal.setScale(2, java.math.RoundingMode.HALF_UP)
  }

  private def preciseAsNumber(number: BigDecimal): java.math.BigDecimal = {
    number.bigDecimal.setScale(4, java.math.RoundingMode.HALF_UP)
  }

  /*
   *  Calculate sum of amount in one account
   */
  private def getPositionValuation(positions: List[Position], ledgerType: LedgerType,
    accValType: AccountValuationType): List[BigDecimal] = {

    val specifiedPositions = findSpecifiedPositions(positions, ledgerType.getDbValue)

    //Find all position with valuation type is market
    //    val posValuationHists = specifiedPositions.map { p =>
    //      positionValuationHistDao.findByPositionIdAsOfDate(
    //        p.getId, StaticDatas.POSITION_VALUATION_TYPE.getDbValue, asOfDate)
    //    }

    val posValuationHists = new java.util.ArrayList[BigDecimal]
    for (pos <- specifiedPositions) {
      val pk = new PositionValuationHist.PK(pos.getId, DefaultValues.POSITION_VALUATION_TYPE.getDbValue(), asOfDate)
      val vh = positionValuationHistDao.findById(pk)
      if (vh != null) 
        posValuationHists.add(vh.getValueAmount())
    }

    //    val filteredHists = {
    //      if (posValuationHists.contains(null)) {
    //        logger.error("Some positions do not have position valuation hist, please check database")
    //        posValuationHists.filter(_ != null)
    //      } else
    //        posValuationHists
    //    }

    // TODO Fix adjust time
    //adjustTs.put(accValType.getDbValue.toInt, findLatestTime(filteredHists.map(_.getAdjustTs)))

    //    filteredHists.map(p => p.getValueAmount)
    posValuationHists.toList
  }

  private def valuatePosition(account: Account,positions: List[Position], ledgerType: LedgerType,
    accValType: AccountValuationType): BigDecimal = {

    val accountId = account.getId
    val list = getPositionValuation(positions, ledgerType, accValType)

    if (list.isEmpty) {
      logger.warn("Cannot find any position in Ledger Type Id: {} for Account Id: {} on {}",
        ledgerType, accountId, asOfDate.toString)
      BigDecimal(0)
    } else {
      val amount = AccountValuationCalc.calculatePosition(list)
      saveAccountValuationHist(
        account,
        accValType,
        preciseAsMoney(amount),
        adjustTs.get(accValType.getDbValue.toInt).get)
      amount
    }
  }

  private def valuateSecurity(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate security")
    val accValType = AccountValuationType.SECURITY
    val ledgerType = LedgerType.SECURITY

    valuatePosition(account, positions, ledgerType, accValType)
  }

  private def valuateCash(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate cash")
    val accValType = AccountValuationType.CASH
    val ledgerType = LedgerType.CASH

    valuatePosition(account, positions, ledgerType, accValType)
  }

  private def valuateCommission(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate commission")
    val accValType = AccountValuationType.COMMISSION
    val ledgerType = LedgerType.COMMISSION

    valuatePosition(account, positions, ledgerType, accValType)
  }

  private def valuateFuturePNLDirection(account: Account,
    positions: List[Position],
    ledgerType: LedgerType): BigDecimal = {
    val specifiedPositions = findSpecifiedPositions(positions, ledgerType.getDbValue)

    //TODO: Our DB doesn't contain a settlement price(结算价)
    //Calculation: (Today's settlement price - yesterday's settlement price)*quantity
    //Now we use price close to do the calculate
    //TODO: need to calculate previous close price, need to time quantity

    val positionFutures = specifiedPositions.map(p => {
      val cvhpk = new CarryingValueHist.PK(p.getId, DefaultValues.CARRYING_VALUE_TYPE, asOfDate)
      val carringValueHist = carryingValueHistDao.findById(cvhpk)

      val carryingValueAmount = {
        if (null == carringValueHist) {
          logger.error("Cannot find Carrring Value Hist for Position Id: {} on {}", p.getId, asOfDate)
          BigDecimal(0)
        } else {
          carringValueHist.getValueAmount
        }
      }

      val pvhpk = new PositionValuationHist.PK(p.getId, DefaultValues.POSITION_VALUATION_TYPE.getDbValue, asOfDate)
      val posValuationHist = positionValuationHistDao.findById(pvhpk)

      val futureValueAmount = {
        if (null == posValuationHist) {
          logger.error("Cannot find Position Valuation Hist for Position Id: {} on {}", p.getId, asOfDate)
          BigDecimal(0)
        } else {
          posValuationHist.getValueAmount
        }
      }

      if (ledgerType.equals(LedgerType.FUTURE_LONG))
        futureValueAmount - carryingValueAmount
      else if (ledgerType.equals(LedgerType.FUTURE_SHORT))
        carryingValueAmount - futureValueAmount
      else {
        logger.error("The Ledger type is neither FutureLong nor FutureShort")
        BigDecimal(0)
      }
    })

    positionFutures.foldLeft(BigDecimal(0))(_ + _)
  }

  private def valuateFuturePNL(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate future PNL")

    val longFuture = valuateFuturePNLDirection(account, positions, LedgerType.FUTURE_LONG)
    val shortFuture = valuateFuturePNLDirection(account, positions, LedgerType.FUTURE_SHORT)

    val futurePNL = AccountValuationCalc.calculateFuture(longFuture, shortFuture)
    //saveAccountValuationHist(account, accValType, future, findLatestAdjustTs)
    futurePNL
  }

  /*
  private def findPriceSettle(positionId: Long): BigDecimal = {
    val secPosition = secPositionDao.findById(positionId)

    if (null == secPosition) {
      logger.error("Cannot find Position Id: {} in SEC_POSITION", positionId)
      BigDecimal(0)
    } else {
      val securityId = secPosition.getSecurityId
      val presentPriceVolume = futPriceVolumeDao.findBySecurityIdTradeDate(securityId, asOfDate)

      if (null == presentPriceVolume) {
        //If today is weekend or holiday, find previous business day
        val previousDate = CalendarUtil.getPreviousBusinessDay(asOfDate)
        val previousPriceVolume = futPriceVolumeDao.findBySecurityIdTradeDate(
          securityId,
          previousDate)

        if (null == previousPriceVolume) {
          logger.error("Cannot find Security Id: {} in FUT_PRICEVOLUME on {} or {}", securityId,
            asOfDate, previousDate)
          BigDecimal(0)
        } else {
          previousPriceVolume.getPriceSettle
        }
      } else {
        //return today's price settle
        presentPriceVolume.getPriceSettle
      }
    }
  }
  */

  private def findPriceSettle(positionId: Long): Double = {
    val secPosition = secPositionDao.findById(positionId)

    if (secPosition != null) {
      val future = futureDao.findById(secPosition.getSecurityId)

      if (future != null) {
        val securityId = secPosition.getSecurityId
        val md = marketDataService.getMarketData(securityId, asOfDate)
        if (md == null) {
          0d
        } else {
          md.getPrice.doubleValue
        }
      } else {
        logger.error("Cannot find Security Id: " + secPosition.getSecurityId +
          "for positionId: " + positionId + " in data base on " + asOfDate)
        0d
      }
    } else {
      logger.error("No Security Position with positionId: " + positionId + "exists in data base")
      0d
    }
  }

  private def valuateFutureValueDirection(positions: List[Position],
    ledgerType: LedgerType): BigDecimal = {

    def getQuantity(positionId: Long): BigDecimal = {
      val positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)
      if (null == positionHist) {
        logger.error("Cannot find Position Id: {} in POSITION_HIST on {}", positionId, asOfDate)
        BigDecimal(0)
      } else {
        positionHist.getQuantity
      }
    }

    val specifiedPositions = findSpecifiedPositions(positions, ledgerType.getDbValue)
    val valueList = specifiedPositions.map(p => {
      val positionId = p.getId
      val price = findPriceSettle(positionId)
      val quantity = getQuantity(positionId)
      price * quantity * DefaultValues.STOCK_INDEX_FUTURE_PRICE_RATIO
    })

    valueList.foldLeft(BigDecimal(0))(_ + _)
  }

  private def valuateFutureLongValue(account: Account,
    positions: List[Position]): BigDecimal = {
    logger.debug("start valuate future long value")
    val accValType = AccountValuationType.FUTURE_LONG_VALUE
    val longFuture = valuateFutureValueDirection(positions, LedgerType.FUTURE_LONG)

    saveAccountValuationHist(account, accValType, preciseAsMoney(longFuture), findLatestAdjustTs)
    longFuture
  }

  private def valuateFutureShortValue(account: Account,
    positions: List[Position]): BigDecimal = {
    logger.debug("start valuate future short value")
    val accValType = AccountValuationType.FUTURE_SHORT_VALUE
    val shortFuture = valuateFutureValueDirection(positions, LedgerType.FUTURE_SHORT)

    saveAccountValuationHist(account, accValType, preciseAsMoney(shortFuture), findLatestAdjustTs)
    shortFuture
  }

  private def valuateFutureValue(account: Account,
    futureLongValue: BigDecimal,
    futureShortValue: BigDecimal): BigDecimal = {

    logger.debug("start valuate future value")
    val accValType = AccountValuationType.FUTURE_VALUE

    val value = futureLongValue + futureShortValue
    saveAccountValuationHist(account, accValType, preciseAsMoney(value), findLatestAdjustTs)
    value
  }

  private def valuateMargin(account: Account,
    positions: List[Position],
    futurePNL: BigDecimal): BigDecimal = {

    logger.debug("start valuate margin")
    val accValType = AccountValuationType.FUTURE_ASSET
    val ledgerType = LedgerType.MARGIN

    val balance = valuatePosition(account, positions, ledgerType, accValType)
    val margin = AccountValuationCalc.calculateMargin(balance, futurePNL)
    saveAccountValuationHist(account, accValType, preciseAsMoney(margin), findLatestAdjustTs)
    margin
  }

  private def valuatePayableSettlement(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate payable settlement")
    val accValType = AccountValuationType.PAYABLE_SETTLEMENT
    val ledgerType = LedgerType.PAYABLE_SETT_ACCOUNTS

    valuatePosition(account, positions, ledgerType, accValType)
  }

  private def valuateReceivableSettlement(account: Account, positions: List[Position]): BigDecimal = {

    logger.debug("start valuate receivable settlement")
    val accValType = AccountValuationType.RECEIVABLE_SETTLEMENT
    val ledgerType = LedgerType.RECEIVABLE_SETT_ACCOUNTS

    valuatePosition(account, positions, ledgerType, accValType)
  }

  private def valuateAsset(account: Account,
    security: BigDecimal,
    cash: BigDecimal,
    margin: BigDecimal,
    receivableSettlement: BigDecimal,
    repoReceivable: BigDecimal): BigDecimal = {

    logger.debug("start valuate asset")
    val accValType = AccountValuationType.ASSET
    val asset = AccountValuationCalc.calculateAsset(security, cash, margin, receivableSettlement, repoReceivable)

    saveAccountValuationHist(account, accValType, preciseAsMoney(asset), findLatestAdjustTs)
    asset
  }

  private def valuateLiability(account: Account,
    payableSettlement: BigDecimal,
    commission: BigDecimal,
    repoPayable: BigDecimal): BigDecimal = {

    logger.debug("start valuate liability")
    val accValType = AccountValuationType.LIABILITY

    val liability = AccountValuationCalc.calculateLiability(payableSettlement, commission, repoPayable)

    saveAccountValuationHist(account, accValType, preciseAsMoney(liability), findLatestAdjustTs)
    liability
  }

  private def valuateNetWorth(account: Account,
    asset: BigDecimal,
    liability: BigDecimal): BigDecimal = {

    logger.debug("start valuate net worth")
    val accValType = AccountValuationType.NET_WORTH
    val netWorth = AccountValuationCalc.calculateNetWorth(asset, liability)

    saveAccountValuationHist(account, accValType, preciseAsMoney(netWorth), findLatestAdjustTs)
    netWorth
  }

  private def findShare(accountId: Long): BigDecimal = {

    //    val position = positionDao.findPositionIdByAccountIdLedgerId(accountId, LedgerType.SHARE.getDbValue)
    val position = cashPositionDao.findByAccountIdLedgerId(accountId, LedgerType.SHARE.getDbValue)

    if (null == position) {
      //TODO: change to error
      logger.error("Share not exist for account Id: " + accountId + " on " + asOfDate.toString)
      BigDecimal(0)
    } else {
      val positionHist = positionHistDao.findByPositionIdAsOfDate(position.getId, asOfDate)

      if (null == positionHist) {
        logger.error("Position Hist not exist for position Id: " + position.getId + " on " + asOfDate.toString)
        BigDecimal(0)
      } else {
        positionHist.getQuantity
      }
    }

  }

  private def valuateUnitNet(account: Account,
    netWorth: BigDecimal): BigDecimal = {

    logger.debug("start valuate unit net")
    val accValType = AccountValuationType.UNIT_NET
    val share = findShare(account.getId)

    //Unit net = Net worth / share
    val unitNet = {
      if (0 == share) {
        logger.error("share is zero for Account Id: " + account.getId + " on " + asOfDate.toString)
        BigDecimal(0)
      } else {
        AccountValuationCalc.calculateUnitNet(netWorth, share)
      }
    }

    saveAccountValuationHist(account, accValType, preciseAsNumber(unitNet), findLatestAdjustTs)
    unitNet
  }

  private def findNetWorth(accountId: Long, asOfDate: LocalDate): BigDecimal = {

    val netWorthAccValType = AccountValuationType.NET_WORTH
    val pk = new AccountValuationHist.PK(accountId, netWorthAccValType.getDbValue(), asOfDate)
    val hist = accountValuationHistDao.findById(pk)

    if (hist != null) {
      hist.getValueAmount
    } else {
      //      val initHist = accountValuationInitDao.findByAccountId(accountId)
      //      if (null == initHist) {
      //        logger.error("Cannot find net worth for account id: " + accountId +
      //          " either in ACCCOUNT_VALUATION_HIST or in ACCOUNT_VALUATION_INIT" +
      //          " on " + asOfDate.toString())
      //        BigDecimal(0)
      //      } else {
      //        initHist.getValueAmount
      //      }
      BigDecimal("0")
    }
  }

  //TODO: Do not save Daily Return in Database, but calculation may be used in other place
  @Deprecated
  def valuateDailyReturn(account: Account, asOfDate: LocalDate): BigDecimal = {

    logger.debug("start valuate daily return")

    val accValType = AccountValuationType.DAILY_RETURN

    val presentNetWorth = findNetWorth(account.getId, asOfDate)
    val previousNetWorth = findNetWorth(account.getId, asOfDate.minusDays(1))

    val dailyReturn = {
      if (0 == previousNetWorth) {
        logger.error("Net Worth on previous day of " + asOfDate + " is zero, please check the data")
        BigDecimal(0)
      } else {
        //每日回报率 = （期末净值 + 当日资金流出 (赎回或者分红）） / （前一日净值+当日资金流入（申购、追加资金等）) - 1
        AccountValuationCalc.calculateDailyReturn(
          presentNetWorth,
          BigDecimal(0),
          previousNetWorth,
          BigDecimal(0))
      }
    }

    saveAccountValuationHist(account, accValType, preciseAsNumber(dailyReturn), findLatestAdjustTs)
    dailyReturn
  }

  //TODO: Do not save PNL in Database, but calculation may be used in other place
  @Deprecated
  private def valuateProfitAndLoss(account: Account): BigDecimal = {

    logger.debug("start valuate PNL")

    val accValType = AccountValuationType.PROFIT_LOSS

    val presentNetWorth = findNetWorth(account.getId, asOfDate)
    val previousNetWorth = findNetWorth(account.getId, asOfDate.minusDays(1))

    //TODO: There is no additional data now
    //期末市值 + 卖出金额 + 分红金额 + 特殊业务流出 - （期初市值 + 买入金额 + 特殊业务流入）
    val pnl = AccountValuationCalc.calculateProfitAndLoss(
      presentNetWorth,
      BigDecimal(0),
      BigDecimal(0),
      BigDecimal(0),
      previousNetWorth,
      BigDecimal(0),
      BigDecimal(0))

    saveAccountValuationHist(account, accValType, preciseAsNumber(pnl), findLatestAdjustTs)
    pnl
  }

  private def valuateShare(account: Account): BigDecimal = {

    val accValType = AccountValuationType.SHARE
    val share = findShare(account.getId)

    saveAccountValuationHist(account, accValType, preciseAsNumber(share), findLatestAdjustTs)
    share
  }

  private def valulateRepo(account: Account, positions: List[Position]): (BigDecimal, BigDecimal, BigDecimal, BigDecimal) = {
    logger.debug("start valuate repo")

    var payPrinciple = valuatePosition(account, positions, LedgerType.PAYABLE_REPO_PRINCIPAL, AccountValuationType.REPO_PRINCIPAL_LIABILITY_VALUE)
    var payInterest = valuatePosition(account, positions, LedgerType.PAYABLE_REPO_INTEREST, AccountValuationType.REPO_INTEREST_LIABILITY_VALUE)
    var receivePrinciple = valuatePosition(account, positions, LedgerType.RECEIVABLE_REPO_PRINCIPAL, AccountValuationType.REPO_PRINCIPAL_ASSET_VALUE)
    var receiveInterest = valuatePosition(account, positions, LedgerType.RECEIVABLE_REPO_INTEREST, AccountValuationType.REPO_INTEREST_ASSET_VALUE)

    (payPrinciple, payInterest, receivePrinciple, receiveInterest)
  }

  private def saveAccountValuationHist(account: Account,
    accValType: AccountValuationType,
    amount: java.math.BigDecimal,
    adjustTs: LocalDateTime): Unit = {

    val now = new Date(System.currentTimeMillis)
    val accountId = account.getId
    val currencyCode = account.getCurrencyCode

    val pk = new AccountValuationHist.PK(accountId, accValType.getDbValue(), asOfDate)
    val hist = accountValuationHistDao.findById(pk)

    if (null == hist) {
      val newHist = new AccountValuationHist(accountId,
        accValType.getDbValue,
        asOfDate,
        amount,
        currencyCode,
        adjustTs //ADJUSTE_TS
        )
      accountValuationHistDao.save(newHist)
    } else {
      hist.setAsOfDate(asOfDate)
      hist.setCurrencyCode(currencyCode)
      hist.setValueAmount(amount)
      hist.setAdjustTs(adjustTs) //ADJUSTE_TS
      accountValuationHistDao.update(hist)
    }
  }
}

