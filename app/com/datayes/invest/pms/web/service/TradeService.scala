package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import com.datayes.invest.pms.dao.account.{AccountDao, OrderDao, SecurityTransactionDao}
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.dbtype.{OrderStatus, TradeSide}
import com.datayes.invest.pms.entity.account.{Order, Account, SecurityTransaction}
import com.datayes.invest.pms.logic.order.OrderManager
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.web.model.models.Trade
import com.weston.jupiter.generated.TradeType
import javax.inject.Inject
import org.joda.time.{LocalDate, LocalDateTime, LocalTime}
import play.pms.ClientException

class TradeService {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var orderDao: OrderDao = null

  @Inject
  private var orderManager: OrderManager = null

  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  def getHistory(accountIdOpt: Option[Long], startDateOpt: Option[LocalDate], endDateOpt: Option[LocalDate]): List[Trade] = transaction {
    val accounts = accountIdOpt match {
      case Some(aid) =>
        val a = accountDao.findById(aid)
        if (a == null) {
          throw new ClientException("Failed to find account by id " + aid)
        }
        List(a)
      case None =>
        val list = accountDao.findEffectiveAccounts(endDateOpt.getOrElse(LocalDate.now()))
        list.toList
    }
    val accountIdList = accounts.map(_.getId)
    val accountMap = accounts.map { a => (a.getId.toLong, a)}.toMap
    val minOpenDate = findMinOpenDate(accounts)

    val transactions = securityTransactionDao.findByAccountIdListBetweenDates(
      accountIdList, startDateOpt.getOrElse(minOpenDate), endDateOpt.getOrElse(LocalDate.now()))

    val (orderTrans, nonOrderTrans) = transactions.partition(_.getOrderId != null)
    val trades = ListBuffer.empty[Trade]
    addNonOrderTransactions(nonOrderTrans, accountMap, trades)
    addOrderTransactions(orderTrans, accountMap, trades)

    val sorted = trades.sortWith { case (t1, t2) => t1.executionDate.isBefore(t2.executionDate) }.toList
    sorted
  }

  private def addOrderTransactions(orderTrans: Seq[SecurityTransaction], accountMap: Map[Long, Account], trades: ListBuffer[Trade]): Unit = {
    val grouped = orderTrans.groupBy { tr => tr.getOrderId.toLong }
    val orderIds = orderTrans.map(_.getOrderId)
    val orders = orderDao.findCurrentListByIds(orderIds)
    val orderMap = orders.map(o => (o.getPk.getId.toLong, o)).toMap

    for ((orderId, transList) <- grouped) {
      val trade = orderTransactionsToTrade(orderId, transList, orderMap, accountMap)
      trades.append(trade)
    }
  }

  private def orderTransactionsToTrade(orderId: Long, transList: Seq[SecurityTransaction], orderMap: Map[Long, Order], accountMap: Map[Long, Account]): Trade = {
    val order = orderMap(orderId)
    val account = accountMap(order.getAccountId.toLong)
    val security = securityDao.findById(order.getSecurityId)
    val assetClass = security.getPmsAssetClass()
    val securityName = if (security.getNameAbbr != null && security.getNameAbbr.trim.nonEmpty) {
      security.getNameAbbr
    } else {
      security.getName
    }
    val tradeSide = TradeSide.fromDbValue(order.getTradeSideCode)
    var executionAmount = BigDecimalConstants.ZERO
    var executionCapital = BigDecimalConstants.ZERO
    for (t <- transList) {
      executionAmount += t.getAmount
      executionCapital += t.getAmount * t.getAvgPrice
    }
    val executionAvgPrice = if (executionAmount.abs < BigDecimalConstants.EPSILON) {
      BigDecimalConstants.ZERO
    } else {
      executionCapital / executionAmount
    }

    val trade = Trade(
      accountId = account.getId,
      accountNo = account.getAccountNo,
      securityName = securityName,
      securitySymbol = security.getTickerSymbol,
      assetClass = assetClass,
      exchange = security.getExchangeCode,
      orderId = Some(order.getPk.getId),
      tradeSide = tradeSide,
      amount = order.getAmount,
      orderPrice = order.getPriceLimit,
      executionAvgPrice = executionAvgPrice,
      executionAmount = executionAmount,
      executionCapital = executionCapital,
      executionDate = order.getAsOfDate
    )

    trade
  }

  private def addNonOrderTransactions(nonOrderTrans: Seq[SecurityTransaction], accountMap: Map[Long, Account], trades: ListBuffer[Trade]): Unit = {
    val grouped = nonOrderTrans.groupBy { tr => (tr.getAccountId,  tr.getSecurityId, tr.getAsOfDate, tr.getTradeSideCode) }
    for (((accountId, securityId, asOfDate, tradeSideCode), transList) <- grouped) {
      val tradeSide = TradeSide.fromDbValue(tradeSideCode)
      val trd = nonOrderTransactionsToTrade(accountId, securityId, asOfDate, tradeSide, transList, accountMap)
      trades.append(trd)
    }
  }

  private def nonOrderTransactionsToTrade(accountId: Long, securityId: Long, asOfDate: LocalDate, tradeSide: TradeSide, transList: Seq[SecurityTransaction], accountMap: Map[Long, Account]): Trade = {
    val account = accountMap(accountId)
    val security = securityDao.findById(securityId)
    val assetClass = security.getPmsAssetClass()
    val securityName = if (security.getNameAbbr != null && security.getNameAbbr.trim.nonEmpty) {
      security.getNameAbbr
    } else {
      security.getName
    }

    // calculate amount and average price
    var amount = BigDecimalConstants.ZERO
    var totalCapital = BigDecimalConstants.ZERO
    for (t <- transList) {
      amount += t.getAmount
      totalCapital += t.getAmount * t.getAvgPrice
    }
    val avgPrice = if (amount.abs < BigDecimalConstants.EPSILON) {
      BigDecimalConstants.ZERO
    } else {
      totalCapital / amount
    }

    // TODO fix this
    val trade = Trade(
      accountId = account.getId,
      accountNo = account.getAccountNo,
      securityName = securityName,
      securitySymbol = security.getTickerSymbol,
      assetClass = assetClass,
      exchange = security.getExchangeCode,
      orderId = None,
      tradeSide = tradeSide,
      amount = amount,
      orderPrice = BigDecimalConstants.ZERO,
      executionAvgPrice = avgPrice,
      executionAmount = amount,
      executionCapital = amount * avgPrice,
      executionDate = asOfDate
    )

    trade
  }

  def placeOrders(basketId: Long, stpAlgorithm: TradeType, stpStartTime: LocalTime, stpEndTime: LocalTime): (String, Long) = transaction {
    val orders = orderDao.findCurrentByBasketId(basketId)
    val today = LocalDate.now()
    // set STP parameters
    for (o <- orders) {
      o.setStpFlag(true)
      o.setStpAlgorithm(stpAlgorithm.toString)
      o.setStpStartTime(today.toLocalDateTime(stpStartTime))
      o.setStpEndTime(today.toLocalDateTime(stpEndTime))
      orderDao.update(o)
    }
    orderManager.placeOrders(basketId)
    // update order status
    val now = LocalDateTime.now()
    for (o <- orders) {
      o.setOrderStatus(OrderStatus.PLACED.getDbValue)
      o.setStatusChangeDate(now)
      orderDao.update(o)
    }

    val accountNo = orders.headOption match {
      case Some(order) =>
        val account = accountDao.findById(order.getAccountId)
        account.getAccountNo
      case None => ""
    }

    (accountNo, basketId)
  }

  private def findMinOpenDate(accounts: List[Account]): LocalDate = {
    var min: LocalDate = null
    for (a <- accounts) {
      if (a.getOpenDate != null) {
        val openDate = a.getOpenDate.toLocalDate
        if (min == null) {
          min = openDate
        } else if (openDate.isBefore(min)) {
          min = openDate
        }
      }
    }

    min
  }
}