package com.datayes.invest.pms.tools.importer

import org.joda.time.LocalDateTime
import org.joda.time.LocalDate

class Context(
  val openDate: LocalDateTime,
  val currency: String,
  var accountId: Long = -1,
  var asOfDate: LocalDate = null
)