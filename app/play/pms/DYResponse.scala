package play.pms

import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.Json

case class DYResponse(
  code: Int,
  message: String,
  data: JsValue
)

object DYResponse {

  implicit object DYResponseWrites extends Writes[DYResponse] {
    def writes(o: DYResponse) = Json.obj(
      "dy_code" -> o.code,
      "dy_message" -> o.message,
      "dy_data" -> o.data
    )
  }
}
