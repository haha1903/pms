package com.datayes.invest.pms.logic.accountinit

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import play.api.libs.json._
import play.api.libs.json.JsString
import scala.Some
import play.api.libs.json.JsSuccess
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.dbtype.LedgerType


case class PositionSourceData (
  positionClass: PositionClass,
  ledgerType: LedgerType,
  openDate: LocalDateTime,
  currencyCode: String,
  quantity: BigDecimal,
  carryingValue: Option[BigDecimal],
  securityId: Option[Long],
  exchangeCode: String
)

object PositionSourceData {

  implicit val dataReader: Reads[PositionSourceData] = Json.reads[PositionSourceData]

  implicit object LocalDateReads extends Reads[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val date = LocalDate.parse(s)
          JsSuccess(date)
        }
        case None => {
          JsError()
        }
      }
    }
  }
  
  implicit object LocalDateTimeReads extends Reads[LocalDateTime] {
    override def reads(json: JsValue): JsResult[LocalDateTime] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val date = LocalDateTime.parse(s)
          JsSuccess(date)
        }
        case None => {
          JsError()
        }
      }
    }
  }

  implicit object PositionClassReads extends Reads[PositionClass] {
    override def reads(json: JsValue): JsResult[PositionClass] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val positionClassType = PositionClass.fromDbValue(s)
          JsSuccess(positionClassType)
        }
        case None => {
          JsError()
        }
      }
    }
  }

  implicit object LedgerTypeReads extends Reads[LedgerType] {
    override def reads(json: JsValue): JsResult[LedgerType] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val ledgerType = LedgerType.valueOf(s)
          JsSuccess(ledgerType)
        }
        case None => {
          JsError()
        }
      }
    }
  }
}
