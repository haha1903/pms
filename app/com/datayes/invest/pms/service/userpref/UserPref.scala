package com.datayes.invest.pms.service.userpref

import com.datayes.invest.pms.web.model.models.AssetNodeType

class UserPref {

  val availablePortfolioGroupingItems: List[GroupingItem] = List(
    GroupingItem(AssetNodeType.account, "组合"),
    GroupingItem(AssetNodeType.assetClass, "资产类型"),
    GroupingItem(AssetNodeType.industry, "行业")
  )

  private var portfolioGroupingSetting = List(AssetNodeType.account)    // Default grouping
    
  private var layoutConfig: String = null

  def getPortfolioGroupingSetting() = portfolioGroupingSetting

  def savePortfolioGroupingSetting(setting: List[AssetNodeType.Type]): Unit = {
    for (s <- setting) {
      val availableItems = availablePortfolioGroupingItems.map(_.nodeType)
      if (! availableItems.contains(s)) {
        throw new RuntimeException("Invalid portfolio grouping setting: " + s)
      }
    }
    this.portfolioGroupingSetting = setting
  }
  
  def getLayoutConfig() = layoutConfig
  
  def saveLayoutConfig(_layoutConfig: String): Unit = {
    layoutConfig = _layoutConfig
  }
}

case class GroupingItem(nodeType: AssetNodeType.Type, displayName: String)