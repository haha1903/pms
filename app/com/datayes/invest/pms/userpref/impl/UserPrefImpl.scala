package com.datayes.invest.pms.userpref.impl

import com.datayes.invest.pms.web.assets.enums.AssetNodeType
import com.datayes.invest.pms.userpref.UserPref
import com.datayes.invest.pms.userpref.GroupingItem
import com.google.inject.Singleton

@Singleton
class UserPrefImpl extends UserPref {

  private val availablePortfolioGroupingItems: List[GroupingItem] = List(
    GroupingItem(AssetNodeType.ACCOUNT, "组合"),
    GroupingItem(AssetNodeType.ASSET_CLASS, "资产类型"),
    GroupingItem(AssetNodeType.INDUSTRY, "行业")
  )

  // Initialize with default grouping
  private var currentPortfolioGroupingSettings = List(AssetNodeType.ACCOUNT)
    
  private var currentLayoutConfig: String = null

  def getCurrentPortfolioGroupingSettings() = currentPortfolioGroupingSettings

  def setPortfolioGroupingSettings(setting: List[AssetNodeType]): Unit = {
    for (s <- setting) {
      val availableItems = availablePortfolioGroupingItems.map(_.nodeType)
      if (! availableItems.contains(s)) {
        throw new RuntimeException("Invalid portfolio grouping setting: " + s)
      }
    }
    this.currentPortfolioGroupingSettings = setting
  }
  
  def getAvailablePortfolioGroupingItems(): List[GroupingItem] = availablePortfolioGroupingItems
  
  def getDashboardLayoutConfig() = currentLayoutConfig
  
  def setDashboardLayoutConfig(layoutConfig: String): Unit = {
    currentLayoutConfig = layoutConfig
  }
}
