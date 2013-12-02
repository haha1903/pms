package com.datayes.invest.pms.web.model

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import com.datayes.invest.pms.dbtype.AccountTypeType
import com.datayes.invest.pms.util.BigDecimalConstants.ZERO
import com.datayes.invest.pms.web.assets.enums.AssetClassType
import play.api.libs.json._
import com.datayes.invest.pms.dbtype.TradeSide

package object models {

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
    assetClass: Option[AssetClassType],
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
    assetClass: AssetClassType,
    var marketValue: BigDecimal = ZERO,
    var weight: BigDecimal = ZERO,
    var floatPnL: BigDecimal = ZERO,
    var floatPnLRate: BigDecimal = ZERO)

  sealed abstract class IndustryWeightNode {
    val name: String
    var marketValue: BigDecimal = ZERO
    var weight: BigDecimal = ZERO
  }

  case class IndustryWeightLeaf(name: String) extends IndustryWeightNode

  case class IndustryWeightTree(name: String, children: Seq[IndustryWeightNode]) extends IndustryWeightNode
  
  case class Holding(name: String, ticker: String) {
    var marketPrice: BigDecimal  = ZERO
    var marketValue: BigDecimal  = ZERO
    var holdingValuePrice: BigDecimal = ZERO
    var industry: String = ""
    var floatPnL: BigDecimal = ZERO
    var weight: BigDecimal = ZERO
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
  	cash: BigDecimal = ZERO
  )
  
  case class Trade(
    accountNo: String,
    securityName: String,
    securitySymbol: String,
    exchange: String,
    tradeSide: TradeSide,
    amount: BigDecimal,
    orderPrice: BigDecimal,
    executionPrice: BigDecimal,
    executionTime: LocalDateTime
  )

  /*
   * Json writes
   */

  object ModelWrites {

    /*
     * Dates
     */

    implicit object LocalDateWrites extends Writes[LocalDate] {
      def writes(o: LocalDate) = JsString(o.toString)
    }

    implicit object LocalDateTimeWrites extends Writes[LocalDateTime] {
      def writes(o: LocalDateTime) = JsString(o.toString)
    }

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
        "assetClass" -> o.assetClass.toString,
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
            json
          case tree: IndustryWeightTree =>
            json ++ Json.obj("children" -> tree.children)
        }
      }
    }
	
	implicit object HoldingWrites extends Writes[Holding] {
      def writes(o: Holding) = Json.obj(
        "name" -> o.name.toString,
        "ticker" -> o.ticker.toString,
        "marketPrice" -> o.marketPrice,
        "marketValue" -> o.marketValue,
        "holdingValuePrice" -> o.holdingValuePrice,
        "industry" -> o.industry.toString,
        "floatPnL" -> o.floatPnL,
        "weight" -> o.weight
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
        val typeName = if( null == o.category) "" else o.category.toString
        Json.obj(
          "count" -> o.count.toString,
          "type" -> typeName,
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
      def writes(o: Trade) = Json.obj(
        "accountNo" -> o.accountNo,
        "securityName" -> o.securityName,
        "securitySymbol" -> o.securitySymbol,
        "exchange" -> o.exchange,
        "tradeSide" -> o.tradeSide.toString(),
        "amount" -> o.amount,
        "orderPrice" -> o.orderPrice,
        "executionPrice" -> o.executionPrice,
        "executionTime" -> o.executionTime
      )
    }
  }
}