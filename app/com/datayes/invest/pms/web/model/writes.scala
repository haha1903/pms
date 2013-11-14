package com.datayes.invest.pms.web.model

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

import play.api.libs.json.JsNull
import play.api.libs.json.JsString
import play.api.libs.json.Writes

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
}