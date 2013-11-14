package com.datayes.invest.pms.userpref

import com.datayes.invest.pms.web.model.models.AssetNodeType

trait UserPref {

  def getPortfolioGroupingSettings(): List[AssetNodeType.Type]
  
  def setPortfolioGroupingSettings(setting: List[AssetNodeType.Type])
  
  def getPortfolioGroupingItems(): List[GroupingItem]
  
  def getDashboardLayoutConfig(): String
  
  def setDashboardLayoutConfig(config: String): Unit
}

case class GroupingItem(nodeType: AssetNodeType.Type, displayName: String)