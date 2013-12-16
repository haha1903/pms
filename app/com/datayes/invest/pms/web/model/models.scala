package com.datayes.invest.pms.web.model

import com.datayes.invest.pms.dbtype.{AccountTypeType, AssetClass, TradeSide}
import com.datayes.invest.pms.util.I18nUtil
import com.datayes.invest.pms.util.BigDecimalConstants.ZERO
import org.joda.time.LocalDate
import play.api.libs.json._

package object models {

  /*
   * Common
   */

  implicit object AssetClassWrites extends Writes[AssetClass] {
    def writes(o: AssetClass) = Json.obj(
      "id" -> o.toString,
      "label" -> I18nUtil.translate_AssetClass(o)
    )
  }

  implicit object AccountTypeTypeWrites extends Writes[AccountTypeType] {
    def writes(o: AccountTypeType) = Json.obj(
      "id" -> o.toString,
      "label" -> I18nUtil.translate_AccountTypeType(o)
    )
  }

  /*
   * Charts
   */

  object ChartType extends Enumeration {
    type Type = Value
    val column, line, pie = Value
  }

  object PortfolioView extends Enumeration {
    type Type = Value
    val industry = Value // only support industry now
  }

  case class ChartDataPoint(name: String, value: BigDecimal)

  case class Chart(chartType: ChartType.Type, data: Seq[ChartDataPoint])

  /*
   * Filter
   */

  case class FilterParam(
    assetClass: Option[AssetClass],
    exchange: Option[String],
    industry: Option[String],
    rangeFilterType: Option[RangeFilterType.Type],
    rangeFilterMax: Option[BigDecimal],
    rangeFilterMin: Option[BigDecimal])

  object RangeFilterType extends Enumeration {
    type Type = Value
    val marketValue, holdingValue, floatPnL = Value
  }

  /*
   * Types for dashboard page
   */

  case class AssetClassWeight(
    assetClass: AssetClass,
    var marketValue: BigDecimal = ZERO,
    var weight: BigDecimal = ZERO,
    var floatPnL: BigDecimal = ZERO,
    var floatPnLRate: BigDecimal = ZERO
  )

  sealed abstract class IndustryWeightNode {
    val name: String
    var marketValue: BigDecimal = ZERO
    var weight: BigDecimal = ZERO
  }

  case class IndustryWeightLeaf(name: String, var benchmarkIndexWeight: BigDecimal = ZERO) extends IndustryWeightNode

  case class IndustryWeightTree(name: String, children: Seq[IndustryWeightNode]) extends IndustryWeightNode
  
  case class Holding(name: String, ticker: String, securityId: Long) {
    var marketPrice: BigDecimal  = ZERO
    var marketValue: BigDecimal  = ZERO
    var holdingValuePrice: BigDecimal = ZERO
    var industry: String = ""
    var floatPnL: BigDecimal = ZERO
    var weight: BigDecimal = ZERO
    var benchmarkIndexWeight: BigDecimal = ZERO
  }
  
  case class TopHoldingStock(
    number: Integer,
    weight: BigDecimal,
    holdings: Seq[Holding])

  case class PortfolioOverview(
    unitNet: BigDecimal,
    changeRate: BigDecimal,
    positionWorth: BigDecimal,
    cash: BigDecimal,
    returnRate: BigDecimal,
    openProfit: BigDecimal)

  case class Performance(
    name: String,
    changeLastWeek: BigDecimal,
    changeLastMonth: BigDecimal, 
    changeLastQuater: BigDecimal,
    changeLastHalfYear: BigDecimal,
    changeLastYear: BigDecimal,
    changeYearToDate: BigDecimal
  )

  case class NetValueTrendItem(
    date: LocalDate,
    netValue: BigDecimal,
    fundReturn: BigDecimal,
    benchmarkReturn: BigDecimal
  )

  case class AccountOverview(
    unitNetValue: BigDecimal,
    dailyReturn: BigDecimal,
    marketValue: BigDecimal,
    cashValue: BigDecimal,
    fundReturn: BigDecimal,
    floatPnL: BigDecimal
  )
  
  case class AccountsSummary(
  	count: Integer,
  	date: LocalDate,
  	totalAssetValue: BigDecimal,
  	totalNetValue: BigDecimal,
  	totalPnl: BigDecimal,
  	totalCash: BigDecimal,
  	items: Seq[AccountsSummaryItem] = null
  )
  
  case class AccountsSummaryItem(
  	count: Integer,
  	category: AccountTypeType,
  	ratio: BigDecimal = ZERO,
  	assetValue: BigDecimal = ZERO,
  	netValue: BigDecimal = ZERO,
  	pnl: BigDecimal = ZERO,
  	cash: BigDecimal = ZERO,
    payableValue: BigDecimal = ZERO,
    receivableValue: BigDecimal = ZERO
  )
  
  case class Trade(
    accountId: Long,
    accountNo: String,
    securityName: String,
    securitySymbol: String,
    assetClass: AssetClass,
    exchange: String,
    orderId: Option[Long],
    tradeSide: TradeSide,
    amount: BigDecimal,
    orderPrice: BigDecimal,
    executionAvgPrice: BigDecimal,
    executionAmount: BigDecimal,
    executionCapital: BigDecimal,
    executionDate: LocalDate
  )

  case class OrderBasket(
    basketId: Long,
    orders: List[Order]
  )

  case class Order(
    orderId: Long,
    accountId: Long,
    accountNo: String,
    assetClass: AssetClass,
    tradeSide: TradeSide,
    securityId: Long,
    securitySymbol: String,
    securityName: String,
    exchange: String,
    amount: Long,
    price: BigDecimal
  )

  /*
   * Json writes
   */

  object ModelWrites {

    /*
     * Charts
     */
    implicit object ChartTypeWrites extends Writes[ChartType.Type] {
      def writes(t: ChartType.Type) = JsString(t.toString)
    }

    implicit object DataPointWrites extends Writes[ChartDataPoint] {
      def writes(p: ChartDataPoint) = Json.obj(
        "name" -> p.name,
        "value" -> p.value
      )
    }

    implicit object ChartWrites extends Writes[Chart] {
      def writes(c: Chart) = Json.obj(
        "chartType" -> c.chartType,
        "data" -> c.data
      )
    }


    /*
     * Dashboard
     */

    implicit object AssetClassWeightWrites extends Writes[AssetClassWeight] {
      def writes(o: AssetClassWeight) = Json.obj(
        "assetClass" -> o.assetClass,
        "weight" -> o.weight,
        "marketValue" -> o.marketValue,
        "floatPnL" -> o.floatPnL,
        "floatPnLRate" -> o.floatPnLRate
      )
    }

    implicit object IndustryWeightNodeWrites extends Writes[IndustryWeightNode] {
      def writes(node: IndustryWeightNode) = {
        val json = Json.obj(
          "name" -> node.name,
          "marketValue" -> node.marketValue,
          "weight" -> node.weight
        )
        node match {
          case leaf: IndustryWeightLeaf =>
            json ++ Json.obj("benchmarkIndexWeight" -> leaf.benchmarkIndexWeight)
          case tree: IndustryWeightTree =>
            json ++ Json.obj("children" -> tree.children)
        }
      }
    }
	
	implicit object HoldingWrites extends Writes[Holding] {
      def writes(o: Holding) = Json.obj(
        "securityId" -> o.securityId,
        "name" -> o.name.toString,
        "ticker" -> o.ticker.toString,
        "marketPrice" -> o.marketPrice,
        "marketValue" -> o.marketValue,
        "holdingValuePrice" -> o.holdingValuePrice,
        "industry" -> o.industry.toString,
        "floatPnL" -> o.floatPnL,
        "weight" -> o.weight,
        "benchmarkIndexWeight" -> o.benchmarkIndexWeight
      )
    }
    
    implicit object TopHoldingStockWrites extends Writes[TopHoldingStock] {
       def writes(o: TopHoldingStock) = Json.obj(
         "number" -> o.number.toInt,
         "weight" -> o.weight,
         "holding" -> o.holdings)
    }
	
    implicit object PerformanceWrites extends Writes[Performance] {
      def writes(o: Performance) = Json.obj(
         "name" -> o.name.toString,
         "changeLastWeek" -> o.changeLastWeek,
         "changeLastMonth" -> o.changeLastMonth,
         "changeLastQuater" -> o.changeLastQuater,
         "changeLastHalfYear" -> o.changeLastHalfYear,
         "changeLastYear" -> o.changeLastYear,
         "changeYearToDate" -> o.changeYearToDate
         )
    }

    implicit object NetValueTrendItemWrites extends Writes[NetValueTrendItem] {
      def writes(o: NetValueTrendItem) = Json.obj(
        "date" -> o.date,
        "netValue" -> o.netValue,
        "fundReturn" -> o.fundReturn,
        "benchmarkReturn" -> o.benchmarkReturn
      )
    }

    implicit object AccountOverviewWrites extends Writes[AccountOverview] {
      // TODO do not expose some fields for now
      def writes(o: AccountOverview) = Json.obj(
        "unitNetValue" -> o.unitNetValue,
        "dailyReturn" -> o.dailyReturn,
        "marketValue" -> o.marketValue,
        "cashValue" -> o.cashValue,
        "fundReturn" -> o.fundReturn,
        "floatPnL" -> o.floatPnL
      )
    }

    implicit object AccountsSummaryItemWrites extends Writes[AccountsSummaryItem] {
      def writes(o: AccountsSummaryItem) = {
        Json.obj(
          "count" -> o.count.toString,
          "type" -> o.category,
          "ratio" -> o.ratio,
          "assetValue" -> o.assetValue,
          "netValue" -> o.netValue,
          "pnl" -> o.pnl,
          "cash" -> o.cash
        )
      }
    }
    
    implicit object AccountsSummaryWrites extends Writes[AccountsSummary] {
      def writes(o: AccountsSummary) = Json.obj(
         "count" -> o.count.toString,
         "date" -> o.date,
         "totalAssetValue" -> o.totalAssetValue,
         "totalNetValue" -> o.totalNetValue,
         "totalPnl" -> o.totalPnl,
         "totalCash" -> o.totalCash,
         "detail" -> o.items
      )
    }
    
    implicit object TradeWrites extends Writes[Trade] {
      def writes(o: Trade) = {
        val sOrderId = o.orderId.map(_.toString).getOrElse("")
        Json.obj(
          "accountId" -> o.accountId,
          "accountNo" -> o.accountNo,
          "securityName" -> o.securityName,
          "securitySymbol" -> o.securitySymbol,
          "assetClass" -> I18nUtil.translate_AssetClass(o.assetClass),
          "exchange" -> o.exchange,
          "orderId" -> sOrderId,
          "tradeSide" -> I18nUtil.translate_TradeSide(o.assetClass, o.tradeSide),
          "amount" -> o.amount,
          "orderPrice" -> o.orderPrice,
          "executionAvgPrice" -> o.executionAvgPrice,
          "executionAmount" -> o.executionAmount,
          "executionCapital" -> o.executionCapital,
          "executionDate" -> o.executionDate
        )
      }
    }

    implicit object OrderWrites extends Writes[Order] {
      def writes(o: Order) = Json.obj(
        "orderId" -> o.orderId,
        "accountId" -> o.accountId,
        "accountNo" -> o.accountNo,
        "tradeSide" -> I18nUtil.translate_TradeSide(o.assetClass, o.tradeSide),
        "securityId" -> o.securityId,
        "securitySymbol" -> o.securitySymbol,
        "securityName" -> o.securityName,
        "exchange" -> o.exchange,
        "amount" -> o.amount,
        "price" -> o.price
      )
    }

    implicit object OrderBasketWrites extends Writes[OrderBasket] {
      def writes(o: OrderBasket) = Json.obj(
        "basketId" -> o.basketId,
        "orders" -> o.orders
      )
    }

  }
}