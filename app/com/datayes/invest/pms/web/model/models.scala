package com.datayes.invest.pms.web.model

import com.datayes.invest.pms.util.BigDecimalConstants.ZERO
import play.api.libs.json._
import org.joda.time.{LocalDateTime, LocalDate}
import com.datayes.invest.pms.dbtype.AccountTypeType

package object models {

  /*
   * Account
   */

//  case class Account(
//    id: Long,
//    name: Option[String],
//    accountNo: Option[String],
//    countryCode: String,
//    currencyCode: String,
//    classCode: String,
//    openDate: Option[LocalDateTime]
//  )


  /*
   * Types for portfolio position tree
   */


  object AssetClassType extends Enumeration {
    type Type = Value
    val none = Value("none")
    val equity = Value("equity")
    val future = Value("future")
    val bond = Value("bond")
    val cash = Value("cash")
    // TODO more asset classes in the future
  }

  object AssetNodeType extends Enumeration {
    type Type = Value
    val leaf = Value("leaf")
    val root = Value("root")

    val account = Value("account")
    val industry = Value("industry")
    val assetClass = Value("assetClass")
  }

  sealed abstract class AssetNode {
    val nodeType: AssetNodeType.Type
    val id: String
    val name: String
    var marketValue: BigDecimal = ZERO
    var weight: BigDecimal = ZERO
    var holdingValue: BigDecimal = ZERO
    var dailyPnL: BigDecimal = ZERO
    var floatPnL: BigDecimal = ZERO
  }

  case class AssetTree(nodeType: AssetNodeType.Type, id: String, name: String, children: Seq[AssetNode]) extends AssetNode

  case class Asset(name: String, code: String, securityId: Long) extends AssetNode {
    override val nodeType = AssetNodeType.leaf
    override val id = securityId.toString
    var marketPrice: BigDecimal = ZERO
    var priceChange: BigDecimal = ZERO
    var holdingQuantity: Long = 0L
    var holdingValuePrice: BigDecimal = ZERO
    var interest: BigDecimal = ZERO
    var earnedPnL: BigDecimal = ZERO
    var benchmarkIndexWeight: BigDecimal = ZERO

    // The following fields are used for grouping and should not be sent to client
    var accountId: Long = 0L
    var accountName: String = ""
    var assetClass: AssetClassType.Type = AssetClassType.none
    var industry: String = ""
    var exchange: String = ""
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
    assetClass: Option[AssetClassType.Type],
    exchange: Option[String],
    industry: Option[String],
    rangeFilterType: Option[RangeFilterType.Type],
    rangeFilterMax: Option[BigDecimal],
    rangeFilterMin: Option[BigDecimal])

  object RangeFilterType extends Enumeration {
    type Type = Value
    val marketValue, holdingValue, dailyPnL, floatPnL = Value
  }

  /*
   * Types for dashboard page
   */

  case class AssetClassWeight(
    assetClass: AssetClassType.Type,
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

  /*
   * Json writes
   */

  object ModelWrites {

    import DecimalWriters.{ writePercentDecimal, writePriceDecimal, writeValueDecimal }

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
     * Account
     */

//    implicit object AccountWrites extends Writes[Account] {
//      def writes(o: Account) = Json.obj(
//        "id" -> o.id,
//        "name" -> o.name,
//        "accountNo" -> o.accountNo,
//        "countryCode" -> o.countryCode,
//        "currencyCode" -> o.currencyCode,
//        "classCode" -> o.classCode,
//        "openDate" -> o.openDate
//      )
//    }


    /*
     * AssetNode
     */

    implicit object AssetNodeWrites extends Writes[AssetNode] {

      def writes(n: AssetNode) = n match {
        case t: AssetTree => assetTreeToJson(t)
        case a: Asset => assetToJson(a)
      }

      private def assetNodeToJson(a: AssetNode) = Json.obj(
        "type" -> a.nodeType.toString,
        "name" -> a.name,
        "id" -> a.id,
        "marketValue" -> writeValueDecimal(a.marketValue),
        "weight" -> writePercentDecimal(a.weight),
        "holdingValue" -> writeValueDecimal(a.holdingValue),
        "dailyPnL" -> writeValueDecimal(a.dailyPnL),
        "floatPnL" -> writeValueDecimal(a.floatPnL)
      )

      private def assetTreeToJson(a: AssetTree) = {
        val json = assetNodeToJson(a)
        json ++ Json.obj(
          "children" -> a.children
        )
      }

      private def assetToJson(a: Asset) = {
        val json = assetNodeToJson(a)
        json ++ Json.obj(
          "securityId" -> a.securityId,
          "code" -> a.code,
          "marketPrice" -> writePriceDecimal(a.marketPrice),
          "priceChange" -> writePercentDecimal(a.priceChange),
          "holdingQuantity" -> writeValueDecimal(a.holdingQuantity),
          "holdingValuePrice" -> writePriceDecimal(a.holdingValuePrice),
          "interest" -> writeValueDecimal(a.interest),
          "earnedPnL" -> writeValueDecimal(a.earnedPnL),
          "benchmarkIndexWeight" -> writePercentDecimal(a.benchmarkIndexWeight)
        )
      }

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
        "value" -> writePercentDecimal(p.value)
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
        "weight" -> writePercentDecimal(o.weight),
        "marketValue" -> writeValueDecimal(o.marketValue),
        "floatPnL" -> writeValueDecimal(o.floatPnL),
        "floatPnLRate" -> writePercentDecimal(o.floatPnLRate)
      )
    }

    implicit object IndustryWeightNodeWrites extends Writes[IndustryWeightNode] {
      def writes(node: IndustryWeightNode) = {
        val json = Json.obj(
          "name" -> node.name,
          "marketValue" -> writeValueDecimal(node.marketValue),
          "weight" -> writePercentDecimal(node.weight)
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
        "marketPrice" -> writePriceDecimal(o.marketPrice),
        "marketValue" -> writeValueDecimal(o.marketValue),
        "holdingValuePrice" -> writePriceDecimal(o.holdingValuePrice),
        "industry" -> o.industry.toString,
        "floatPnL" -> writePercentDecimal(o.floatPnL),
        "weight" -> writePercentDecimal(o.weight))
    }
    
    implicit object TopHoldingStockWrites extends Writes[TopHoldingStock] {
       def writes(o: TopHoldingStock) = Json.obj(
         "number" -> o.number.toInt,
         "weight" -> writePercentDecimal(o.weight),
         "holding" -> o.holdings)
    }
	
    implicit object PerformanceWrites extends Writes[Performance] {
      def writes(o: Performance) = Json.obj(
         "name" -> o.name.toString,
         "changeLastWeek" -> writePercentDecimal(o.changeLastWeek),
         "changeLastMonth" -> writePercentDecimal(o.changeLastMonth),
         "changeLastQuater" -> writePercentDecimal(o.changeLastQuater),
         "changeLastHalfYear" -> writePercentDecimal(o.changeLastHalfYear),
         "changeLastYear" -> writePercentDecimal(o.changeLastYear),
         "changeYearToDate" -> writePercentDecimal(o.changeYearToDate)
         )
    }

    implicit object NetValueTrendItemWrites extends Writes[NetValueTrendItem] {
      def writes(o: NetValueTrendItem) = Json.obj(
        "date" -> o.date,
        "netValue" -> writeValueDecimal(o.netValue),
        "fundReturn" -> writePercentDecimal(o.fundReturn),
        "benchmarkReturn" -> writePercentDecimal(o.benchmarkReturn)
      )
    }

    implicit object AccountOverviewWrites extends Writes[AccountOverview] {
      // TODO do not expose some fields for now
      def writes(o: AccountOverview) = Json.obj(
        "unitNetValue" -> writeValueDecimal(o.unitNetValue),
        "dailyReturn" -> writePercentDecimal(o.dailyReturn),
        "marketValue" -> writeValueDecimal(o.marketValue),
        "cashValue" -> writeValueDecimal(o.cashValue),
        "fundReturn" -> writePercentDecimal(o.fundReturn),
        "floatPnL" -> writeValueDecimal(o.floatPnL)
      )
    }

    implicit object AccountsSummaryItemWrites extends Writes[AccountsSummaryItem] {
      def writes(o: AccountsSummaryItem) = Json.obj(
         "count" -> o.count.toString,
         "type" -> categoryMap.get(o.category).getOrElse("").toString,
         "ratio" -> writePercentDecimal(o.ratio),
         "assetValue" -> writeValueDecimal(o.assetValue),
         "netValue" -> writeValueDecimal(o.netValue),
         "pnl" -> writeValueDecimal(o.pnl),
         "cash" -> writeValueDecimal(o.cash)
      )
      private val categoryMap: Map[AccountTypeType, String] = Map(
        AccountTypeType.ABSOLUTE_INCOME -> "AbsoluteIncome",
        AccountTypeType.CASH_INCOME -> "CashIncome",
        AccountTypeType.EQUITY -> "Equity",
        AccountTypeType.FIXED_INCOME -> "FixedIncome"
      )
    }
    
    implicit object AccountsSummaryWrites extends Writes[AccountsSummary] {
      def writes(o: AccountsSummary) = Json.obj(
         "count" -> o.count.toString,
         "date" -> o.date,
         "totalAssetValue" -> writeValueDecimal(o.totalAssetValue),
         "totalNetValue" -> writeValueDecimal(o.totalNetValue),
         "totalPnl" -> writeValueDecimal(o.totalPnl),
         "totalCash" -> writeValueDecimal(o.totalCash),
         "detail" -> o.items
      )
    }
    
  }
}