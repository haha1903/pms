package com.datayes.invest.pms.web.model

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import play.api.libs.json.JsNull
import play.api.libs.json.JsString
import play.api.libs.json.Writes
import com.datayes.invest.pms.entity.account.Account
import play.api.libs.json.Json
import com.datayes.invest.pms.service.marketindex.Index

package object writes {

  implicit object LocalDateWrites extends Writes[LocalDate] {
    
    def writes(o: LocalDate) = if (o == null) {
      JsNull
    } else {
      JsString(o.toString())
    }
  }
  
  implicit object LocalDateTimeWrites extends Writes[LocalDateTime] {
    
    def writes(o: LocalDateTime) = if (o == null) {
      JsNull
    } else {
      JsString(o.toString())
    }
  }
  
  implicit object AccountWrites extends Writes[Account] {
    
    def writes(o: Account) = Json.obj(
      "id" -> o.getId().toLong,
      "name" -> o.getAccountName(),
      "accountNo" -> o.getAccountNo(),
      "countryCode" -> o.getCountryCode(),
      "currencyCode" -> o.getCurrencyCode(),
      "classCode" -> o.getAccountClass(),
      "openDate" -> o.getOpenDate()
    )
  }

  implicit object IndexWrites extends Writes[Index] {

    def writes(o: Index) = Json.obj(
      "id" -> o.getId,
      "name" -> o.getName
    )
  }
}