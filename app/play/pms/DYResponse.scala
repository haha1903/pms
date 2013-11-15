package play.pms

import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.Json

case class DYJsResponse(
  code: Int,
  message: String,
  data: JsValue
)

object DYJsResponse {

  implicit object DYJsResponseWrites extends Writes[DYJsResponse] {
    def writes(o: DYJsResponse) = Json.obj(
      "dy_code" -> o.code,
      "dy_message" -> o.message,
      "dy_data" -> o.data
    )
  }
}

object DYStrResponse {
  
  def create(code: Int, message: String, json: String): String = {
    val b = new StringBuilder
    
    b.append("{")
    b.append("\"dy_code\": ")
    b.append(code.toString())
    b.append(", ")
    b.append("\"dy_message\": ")
    b.append('"')
    b.append(message)
    b.append('"')
    b.append(", ")
    b.append("\"dy_data\": ")
    b.append(json)
    b.append("}")
    
    b.toString()
  }
}
