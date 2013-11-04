package com.datayes.invest.pms.logic.accountinit

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import play.api.libs.json._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsString
import com.datayes.invest.pms.dbtype.AccountClassType
import com.datayes.invest.pms.dbtype.AccountTypeType

case class AccountSourceData (
  val partyId: Long,
  val parentAccountId: Option[Long],
  val countryCode: String,
  val currencyCode: String,
  val classCode: AccountClassType,
  val accountType: AccountTypeType,
  val accountNo: String,
  val name: String,
  val openDate: LocalDateTime,
  val netWorth: BigDecimal,
  val share: BigDecimal,
  val positions: List[PositionSourceData],
  val fees: List[RateSourceData]
)

object AccountSourceData {

  implicit val dataReader: Reads[AccountSourceData] = Json.reads[AccountSourceData]

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

  implicit object AccountClassReads extends Reads[AccountClassType] {
    override def reads(json: JsValue): JsResult[AccountClassType] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val accountClassType = AccountClassType.fromDbValue(s)
          JsSuccess(accountClassType)
        }
        case None => {
          JsError()
        }
      }
    }
  }

  implicit object AccountTypeReads extends Reads[AccountTypeType] {
    override def reads(json: JsValue): JsResult[AccountTypeType] = {
      json.asOpt[JsString] match {
        case Some(jsString) => {
          val s = jsString.value
          val accountTypeType = AccountTypeType.valueOf(s)
          JsSuccess(accountTypeType)
        }
        case None => {
          JsError()
        }
      }
    }
  }
}