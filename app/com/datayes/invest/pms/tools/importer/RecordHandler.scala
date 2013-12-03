package com.datayes.invest.pms.tools.importer

import com.datayes.invest.pms.logic.accountinit.PositionSourceData
import com.datayes.invest.pms.entity.account.SourceTransaction

trait RecordHandler {

  def matches(context: Context, values: Array[String]): Boolean
  
  def createInitialPosition(context: Context, values: Array[String]): PositionSourceData
  
  def createSourceTransaction(context: Context, values: Array[String]): SourceTransaction

}