package play.pms

class ClientException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  
  def this() = this(null, null)
  
  def this(message: String) = this(message, null)
  
  def this(cause: Throwable) = this(cause.getMessage(), cause)
}

class MissingParamException(name: String) extends ClientException {
    
  override def getMessage(): String = "Missing parameter: " + name
}

class ParamFormatException(name: String, typ: String, value: String, cause: Throwable) extends ClientException(cause) {
  
  override def getMessage(): String = "Parameter format error: " + name + " should be of type " + typ + " but was " + value
}