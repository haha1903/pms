package com.datayes.invest.pms.web.assets

import scala.collection.mutable

import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.web.assets.enums.AssetClassType
import com.datayes.invest.pms.web.assets.enums.AssetNodeType
import com.datayes.invest.pms.web.assets.enums.LongShort

package object models {

  trait Filterable {
    var accountId: Long = -1
    var assetClass: AssetClassType = null
    var exchange: String = null
    var industry: String = null
  }
  
  abstract class AssetNode {
    def nodeType: AssetNodeType
    def name: String
    
    // these field are roll-up-able
    var marketValue: BigDecimal = BigDecimalConstants.ZERO
    var holdingValue: BigDecimal = BigDecimalConstants.ZERO
    var floatPnL: BigDecimal = BigDecimalConstants.ZERO
    var weight: BigDecimal = BigDecimalConstants.ZERO
  }
  
  class AssetTree(
    val nodeType: AssetNodeType,
    val name: String
    
  ) extends AssetNode {
    
    var children: Seq[AssetNode] = mutable.ListBuffer.empty[AssetNode]
  }
  
  abstract class AssetCommon extends AssetNode with Filterable {
    def securityId: Long
    def code: String
    def name: String
    
    // this is for front-end to determine the leaf node
    val leaf: Boolean = true
    
    var holdingQuantity: Long = 0L
    var holdingValuePrice: BigDecimal = null
    var marketPrice: BigDecimal = null
  }
  
  class AssetCash(val name: String) extends AssetCommon {
    // override super classes abstract defs
    val securityId = -1L
    val code = name
    val nodeType = AssetNodeType.LEAF
  }
  
  class AssetEquity(
    val securityId: Long,
    val code: String,
    val name: String
    
  ) extends AssetCommon {
    
    var priceChange: BigDecimal = null
    var dailyPnL: BigDecimal = null
    var tradePnL: BigDecimal = null
    var holdingPnL: BigDecimal = null
    var benchmarkIndexWeight: BigDecimal = null
    
    // override super classes abstract defs
    val nodeType = AssetNodeType.LEAF
  }
  
  class AssetIndexFuture(
    val securityId: Long,
    val code: String,
    val name: String
    
  ) extends AssetCommon {
    
    var longShort: LongShort = null
    var marginOccupied: BigDecimal = null
    
    // override super classes abstract defs
    val nodeType = AssetNodeType.LEAF
  }
}