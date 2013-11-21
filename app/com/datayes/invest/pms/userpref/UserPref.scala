package com.datayes.invest.pms.userpref

import com.datayes.invest.pms.web.assets.enums.AssetNodeType


trait UserPref {

  def getCurrentPortfolioGroupingSettings(): List[AssetNodeType]
  
  def setPortfolioGroupingSettings(setting: List[AssetNodeType])
  
  def getAvailablePortfolioGroupingItems(): List[GroupingItem]
  
  def getDashboardLayoutConfig(): String
  
  def setDashboardLayoutConfig(config: String): Unit
}

case class GroupingItem(nodeType: AssetNodeType, displayName: String)