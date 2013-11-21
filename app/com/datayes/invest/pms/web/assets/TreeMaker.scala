package com.datayes.invest.pms.web.assets

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.assets.enums.AssetNodeType
import scala.collection.mutable.ListBuffer
import com.datayes.invest.pms.web.assets.models._
import com.datayes.invest.pms.entity.account.Account


class TreeMaker(groupings: List[AssetNodeType], accounts: Seq[Account] /*accountIdNameMap: Map[Long, String]*/) extends Logging {
  
  private val accountIdNameMap: Map[Long, String] = createAccountIdNameMap(accounts)

  def create(assets: Seq[AssetCommon]): AssetTree = {
    val validGroupings = validateGroupings(groupings)
    val trees = makeTreeRecursivly(assets, validGroupings)
    val root = new AssetTree(AssetNodeType.ROOT, "ROOT", "ROOT")
    root.children = trees
    
    rollup(root)
    logger.debug("root.marketValue: {}", root.marketValue)
//    if (root.marketValue > 0) {
//      calculateWeight(root, root.marketValue)
//    }    // TODO how the weight should be calculated?
    root
  }

//  private def calculateWeight(node: AssetNode, totalMarketValue: BigDecimal): Unit = {
//    node.weight = node.marketValue / totalMarketValue
//    node match {
//      case tree: AssetTree =>
//        tree.children foreach { calculateWeight(_, totalMarketValue) }
//      case _ => return
//    }
//  }

  private def rollup(node: AssetNode): Unit = node match {
    case asset: AssetCommon =>
      return
    case tree: AssetTree =>
      for (n <- tree.children) {
        rollup(n)
        node.marketValue += n.marketValue
        node.holdingValue += n.holdingValue
        node.floatPnL += n.floatPnL
        node.weight += n.weight
      }
  }

  private def makeTreeRecursivly(assets: Seq[AssetCommon], groups: List[AssetNodeType]): Seq[AssetNode] = groups match {
    case Nil =>
      assets
    case t :: ts =>
      val (nodeType, groupList: List[(IdName, Seq[AssetCommon])]) = t match {
        case AssetNodeType.ACCOUNT => (t, groupByAccount(assets))
        case AssetNodeType.ASSET_CLASS => (t, groupByAssetClass(assets))
        case AssetNodeType.INDUSTRY => (t, groupByIndustry(assets))
        case x => throw new RuntimeException("Coding error. Unexpected AssetNodeType: " + x)
      }
      val trees = groupList map { case (idName, seq) =>
        val children = makeTreeRecursivly(seq, ts)
        val tr = new AssetTree(nodeType, idName.id, idName.name)
        tr.children = children
        tr
      }
      trees.toSeq
  }

  case class IdName(id: String, name: String)

  private def groupByAccount(assets: Seq[AssetCommon]): List[(IdName, Seq[AssetCommon])] = {
    val list = assets.groupBy { a => a.accountId }.toList
    list.map { case (id, seq) =>
      val accountName = accountIdNameMap.getOrElse(id, "")
      (IdName(id.toString, accountName), seq)
    }
  }

  private def groupByAssetClass(assets: Seq[AssetCommon]): List[(IdName, Seq[AssetCommon])] = {
    val list = assets.groupBy { a => a.assetClass.toString }.toList
    list.map { case (name, seq) =>
      (IdName(name, name), seq)
    }
  }

  private def groupByIndustry(assets: Seq[AssetCommon]): List[(IdName, Seq[AssetCommon])] = {
    val list = assets.groupBy { a => a.industry }.toList
    list.map { case (name, seq) =>
      (IdName(name, name), seq)
    }
  }

  private def validateGroupings(groupings: List[AssetNodeType]): List[AssetNodeType] = {
    val filtered = groupings.filter { g => g != AssetNodeType.ROOT && g != AssetNodeType.LEAF }
    filtered
  }
  
  private def createAccountIdNameMap(accounts: Seq[Account]): Map[Long, String] =
    (for (a <- accounts) yield (a.getId.toLong -> a.getAccountName)).toMap
}
