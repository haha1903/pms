package com.datayes.invest.pms.util

import play.api.i18n.Messages
import com.datayes.invest.pms.dbtype.{TradeSide, AssetClass}
import scala.reflect.runtime.{universe => ru}
import com.datayes.invest.pms.web.assets.enums.AssetNodeType

object I18nUtil {

  def translate_AssetNodeType_ROOT(): String = getMessage[AssetNodeType]("ROOT")  //Messages(getClassName[AssetNodeType] + ".ROOT")

  def translate_AssetClass(assetClass: AssetClass): String = getMessage[AssetClass](assetClass)  // Messages(getClassName[AssetClass] + "." + t.toString)

  def translate_TradeSide(assetClass: AssetClass, tradeSide: TradeSide): String = getMessage[TradeSide](assetClass, tradeSide)
    //Messages(getClassName[TradeSide] + "." + assetClass.toString + "." + tradeSide.toString)

  private def getMessage[T: ru.TypeTag](args: Any*): String = {
    val key = getClassName[T] + "." + args.map(_.toString).mkString(".")
    Messages(key)
  }

  private def getClassName[T: ru.TypeTag]: String = {
    val ttg = ru.typeTag[T]
    val s = ttg.tpe.typeSymbol.asClass.name.toString
    println(s)
    s
  }
}
