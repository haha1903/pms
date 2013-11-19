package com.datayes.invest.pms.web.model.fastjson.asset

import com.datayes.invest.pms.web.model.models.{ Asset => PAsset, AssetNode => PAssetNode, AssetTree => PAssetTree}

object AssetTreeConverter {

  def toFastJsonObject(assetTree: PAssetTree): AssetTree =
    convertAssetNode(assetTree).asInstanceOf[AssetTree]

  private def convertAssetNode(assetNode: PAssetNode): AssetNode = {
    val node = assetNode match {
      case tree: PAssetTree => convertAssetTree(tree)
      case asset: PAsset => convertAsset(asset)
    }
    node.dailyPnL = assetNode.dailyPnL.bigDecimal
    node.floatPnL = assetNode.floatPnL.bigDecimal
    node.holdingValue = assetNode.holdingValue.bigDecimal
    node.marketValue = assetNode.marketValue.bigDecimal
    node.weight = assetNode.weight.bigDecimal
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

    a.marketPrice = asset.marketPrice.bigDecimal
    a.priceChange = asset.priceChange.bigDecimal
    a.holdingQuantity = asset.holdingQuantity
    a.holdingValuePrice = asset.holdingValuePrice.bigDecimal
    a.interest = asset.interest.bigDecimal
    a.earnedPnL = asset.earnedPnL.bigDecimal
    a.benchmarkIndexWeight = asset.benchmarkIndexWeight.bigDecimal

    a
  }
}
