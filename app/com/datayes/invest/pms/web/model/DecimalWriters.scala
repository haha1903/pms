package com.datayes.invest.pms.web.model

import java.text.DecimalFormat

object DecimalWriters {

  private def createFormatter(f: String): ThreadLocal[DecimalFormat] = new ThreadLocal[DecimalFormat] {
    override def initialValue = new DecimalFormat(f)
  }

  private val percentDecimalFormatter = createFormatter("#.####")

  private val valueDecimalFormatter = createFormatter("#.##")

  private val priceDecimalFormatter = createFormatter("#.##")

  def writePercentDecimal(d: BigDecimal): String = percentDecimalFormatter.get.format(d)

  def writeValueDecimal(d: BigDecimal): String = valueDecimalFormatter.get.format(d)

  def writePriceDecimal(d: BigDecimal): String = priceDecimalFormatter.get.format(d)

}
