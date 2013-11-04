package com.datayes.invest.pms.web.model.fastjson.asset

import com.datayes.invest.pms.web.model.models.{ Asset => PAsset, AssetNode => PAssetNode, AssetTree => PAssetTree}
import controllers.util.DecimalWriters.{ writePercentDecimal, writePriceDecimal, writeValueDecimal }


object AssetTreeConverter {

  def toFastJsonObject(assetTree: PAssetTree): AssetTree =
    convertAssetNode(assetTree).asInstanceOf[AssetTree]

  private def convertAssetNode(assetNode: PAssetNode): AssetNode = {
    val node = assetNode match {
      case tree: PAssetTree => convertAssetTree(tree)
      case asset: PAsset => convertAsset(asset)
    }
    node.dailyPnL = writeValueDecimal(assetNode.dailyPnL)
    node.floatPnL = writeValueDecimal(assetNode.floatPnL)
    node.holdingValue = writeValueDecimal(assetNode.holdingValue)
    node.marketValue = writeValueDecimal(assetNode.marketValue)
    node.weight = writePercentDecimal(assetNode.weight)
    node
  }

  private def convertAssetTree(assetTree: PAssetTree): AssetTree = {
    val children = new java.util.ArrayList[AssetNode]
    val nodeType = AssetNodeType.valueOf(assetTree.nodeType.toString)
    for (c <- assetTree.children) {
      val nc = convertAssetNode(c)
      children.add(nc)
    }
    val t = new AssetTree(nodeType, assetTree.id, assetTree.name, children);
    t
  }

  private def convertAsset(asset: PAsset): Asset = {
    val a = new Asset(asset.name, asset.code, asset.securityId);

    a.marketPrice = writePriceDecimal(asset.marketPrice)
    a.priceChange = writePercentDecimal(asset.priceChange)
    a.holdingQuantity = asset.holdingQuantity
    a.holdingValuePrice = writeValueDecimal(asset.holdingValuePrice)
    a.interest = writeValueDecimal(asset.interest)
    a.earnedPnL = writeValueDecimal(asset.earnedPnL)
    a.benchmarkIndexWeight = writePercentDecimal(asset.benchmarkIndexWeight)

    a
  }
}
