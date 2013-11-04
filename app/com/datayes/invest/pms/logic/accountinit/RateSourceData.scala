package com.datayes.invest.pms.logic.accountinit

import play.api.libs.json._
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.TradeSide


case class RateSourceData (
  val rateType: RateType,
  val tradeSide: Option[TradeSide],
  val securityId: Option[Long],
  val rate: BigDecimal
)

object RateSourceData {

  implicit val dataReader: Reads[RateSourceData] = Json.reads[RateSourceData]

  implicit object TradeSideReads extends Reads[TradeSide] {
    override def reads(json: JsValue): JsResult[TradeSide] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val tradeSide = TradeSide.valueOf(s)
          JsSuccess(tradeSide)
        }
        case None => {
          JsSuccess(null)
        }
      }
    }
  }


  implicit object RateTypeReads extends Reads[RateType] {
    override def reads(json: JsValue): JsResult[RateType] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val rateType = RateType.valueOf(s)
          JsSuccess(rateType)
        }
        case None => {
          JsError()
        }
      }
    }
  }

}
