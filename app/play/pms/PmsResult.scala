package play.pms

import play.api.libs.json.JsValue

object PmsResult {
  
  def apply(json: JsValue) = PmsJsResult(json)
  
  def apply(s: String) = PmsStrResult(s)
}

sealed abstract class PmsResult

case class PmsJsResult(json: JsValue) extends PmsResult

case class PmsStrResult(content: String) extends PmsResult
