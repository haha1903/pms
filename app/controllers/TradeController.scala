package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.model.models.ModelWrites._
import com.datayes.invest.pms.web.model.models.{ Order => MOrder, OrderBasket => MOrderBasket }
import com.datayes.invest.pms.web.service.TradeService
import javax.inject.Inject
import org.joda.time.LocalDate
import play.api.libs.json.Json
import com.datayes.invest.pms.tools.importer.order.OrderImporter
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.service.order.OrderBasket
import com.datayes.invest.pms.service.order.Order
import play.pms._
import scala.collection.JavaConversions._

class TradeController extends PmsController with Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var tradeService: TradeService = null

  @Inject
  private var orderImporter: OrderImporter = null

  @Inject
  private var securityDao: SecurityDao = null

  def history() = PmsAction { implicit req =>
    val accountIdOpt: Option[Long] = param("accountId")
    val startDateOpt: Option[LocalDate] = param("startDate")
    val endDateOpt: Option[LocalDate] = param("endDate")

    val history = tradeService.getHistory(accountIdOpt, startDateOpt, endDateOpt)
    val json = Json.toJson(history)

    PmsResult(json)
  }

  def orderImport = PmsAction(parse.multipartFormData) { implicit req =>
    transaction {
      val accountId = {
        val name = "accountId"
        val sAccountId = req.body.dataParts.get(name).flatMap(_.headOption).getOrElse(throw new MissingParamException("accountId"))
        try {
          java.lang.Long.parseLong(sAccountId)
        } catch {
          case e: IllegalArgumentException => throw new ParamFormatException(name, "long", sAccountId, e)
        }
      }

      val account = accountDao.findById(accountId)

      println("dataParts: " + req.body.dataParts)

      if (account == null) {
        throw new ClientException("Invalid account id " + accountId)
      }

      try {

        req.body.file("upload").map { file =>
          val tmpFile = file.ref.file
          logger.debug("Order import csv file received: " + tmpFile)
          val orderBasket = orderImporter.importCsv(accountId, tmpFile)
          val mOrderBasket = convertOrderBasket(orderBasket, account)
          val json = Json.toJson(mOrderBasket)
          PmsResult(json)

        }.getOrElse {
          throw new ClientException("Failed to import account. Missing import csv file")
        }
      } catch {
        case e: Throwable =>
          logger.debug("Failed to import account: " + e.getMessage, e)
          throw new RuntimeException("Failed to import account: " + e.getMessage(), e)
      }
    }
  }


  private def convertOrderBasket(basket: OrderBasket, account: Account): MOrderBasket = {
    val mOrders = basket.getOrders.map(o => convertOrder(o, account)).toList
    val mBasket = MOrderBasket(basket.getId, mOrders)
    mBasket
  }

  private def convertOrder(order: Order, account: Account): MOrder = {
    val security = securityDao.findById(order.getSecurityId)
    val securityName = if (security.getNameAbbr == null || security.getNameAbbr.trim.isEmpty) {
      security.getName
    } else {
      security.getNameAbbr
    }

    val mOrder = MOrder(
      orderId = order.getOrderId,
      accountId = order.getAccountId,
      accountNo = account.getAccountNo,
      assetClass = security.getPmsAssetClass,
      tradeSide = order.getTradeSide,
      securityId = order.getSecurityId,
      securitySymbol = security.getTickerSymbol,
      securityName = securityName,
      exchange = security.getExchangeCode,
      amount = order.getAmount,
      price = order.getPriceLimit
    )

    mOrder
  }

}