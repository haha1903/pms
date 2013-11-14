package play.pms


class MissingParamException(name: String) extends RuntimeException {
    
  override def getMessage(): String = "Missing parameter: " + name
}

class ParamFormatException(name: String, typ: String, value: String, cause: Throwable) extends RuntimeException(cause) {
  
  override def getMessage(): String = "Parameter format error: " + name + " should be of type " + typ + " but was " + value
}