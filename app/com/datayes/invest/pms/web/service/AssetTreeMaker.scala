package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.web.model.models.{AssetNode, Asset, AssetTree, AssetNodeType}
import com.datayes.invest.pms.logging.Logging

class AssetTreeMaker(groupings: List[AssetNodeType.Type], accountIdNameMap: Map[Long, String]) extends Logging {

  def create(assets: Seq[Asset]): AssetTree = {
    val validGroupings = validateGroupings(groupings)
    val trees = makeTreeRecursivly(assets, validGroupings)
    val root = AssetTree(AssetNodeType.root, "root", "root", trees)
    rollup(root)
    logger.debug("root.marketValue: {}", root.marketValue)
    if (root.marketValue > 0) {
      calculateWeight(root, root.marketValue)
    }    // TODO how the weight should be calculated?
    root
  }

  private def calculateWeight(node: AssetNode, totalMarketValue: BigDecimal): Unit = {
    node.weight = node.marketValue / totalMarketValue
    node match {
      case tree: AssetTree =>
        tree.children foreach { calculateWeight(_, totalMarketValue) }
      case _ => return
    }
  }

  private def rollup(node: AssetNode): Unit = node match {
    case asset: Asset =>
      return
    case tree: AssetTree =>
      for (n <- tree.children) {
        rollup(n)
        node.marketValue += n.marketValue
        node.holdingValue += n.holdingValue
        node.dailyPnL += n.dailyPnL
        node.floatPnL += n.floatPnL
      }
  }

  private def makeTreeRecursivly(assets: Seq[Asset], groups: List[AssetNodeType.Type]): Seq[AssetNode] = groups match {
    case Nil =>
      assets
    case g :: gs =>
      val (nodeType, groupList: List[(IdName, Seq[Asset])]) = g match {
        case AssetNodeType.account => (AssetNodeType.account, groupByAccount(assets))
        case AssetNodeType.assetClass => (AssetNodeType.assetClass, groupByAssetClass(assets))
        case AssetNodeType.industry => (AssetNodeType.industry, groupByIndustry(assets))
        case x => throw new RuntimeException("Coding error. Unexpected AssetNodeType: " + x)
      }
      val trees = groupList map { case (idName, seq) =>
        val children = makeTreeRecursivly(seq, gs)
        val t = AssetTree(nodeType, idName.id, idName.name, children)
        t
      }
      trees.toSeq
  }

  case class IdName(id: String, name: String)

  private def groupByAccount(assets: Seq[Asset]): List[(IdName, Seq[Asset])] = {
    val list = assets.groupBy { a => a.accountId }.toList
    list.map { case (id, seq) =>
      val accountName = accountIdNameMap.getOrElse(id, "")
      (IdName(id.toString, accountName), seq)
    }
  }

  private def groupByAssetClass(assets: Seq[Asset]): List[(IdName, Seq[Asset])] = {
    val list = assets.groupBy { a => a.assetClass.toString }.toList
    list.map { case (name, seq) =>
      (IdName(name, name), seq)
    }
  }

  private def groupByIndustry(assets: Seq[Asset]): List[(IdName, Seq[Asset])] = {
    val list = assets.groupBy { a => a.industry }.toList
    list.map { case (name, seq) =>
      (IdName(name, name), seq)
    }
  }

  private def validateGroupings(groupings: List[AssetNodeType.Type]): List[AssetNodeType.Type] = {
    val filtered = groupings.filter { g => g != AssetNodeType.root && g != AssetNodeType.leaf }
    filtered
  }
}
