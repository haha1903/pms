package com.datayes.invest.pms.userpref.impl

import com.datayes.invest.pms.web.model.models.AssetNodeType
import com.datayes.invest.pms.userpref.UserPref
import com.datayes.invest.pms.userpref.GroupingItem
import com.google.inject.Singleton

@Singleton
class UserPrefImpl extends UserPref {

  private val availablePortfolioGroupingItems: List[GroupingItem] = List(
    GroupingItem(AssetNodeType.account, "组合"),
    GroupingItem(AssetNodeType.assetClass, "资产类型"),
    GroupingItem(AssetNodeType.industry, "行业")
  )

  // Initialize with default grouping
  private var currentPortfolioGroupingSettings = List(AssetNodeType.account)
    
  private var currentLayoutConfig: String = null

  def getPortfolioGroupingSettings() = currentPortfolioGroupingSettings

  def setPortfolioGroupingSettings(setting: List[AssetNodeType.Type]): Unit = {
    for (s <- setting) {
      val availableItems = availablePortfolioGroupingItems.map(_.nodeType)
      if (! availableItems.contains(s)) {
        throw new RuntimeException("Invalid portfolio grouping setting: " + s)
      }
    }
    this.currentPortfolioGroupingSettings = setting
  }
  
  def getPortfolioGroupingItems(): List[GroupingItem] = availablePortfolioGroupingItems
  
  def getDashboardLayoutConfig() = currentLayoutConfig
  
  def setDashboardLayoutConfig(layoutConfig: String): Unit = {
    currentLayoutConfig = layoutConfig
  }
}
