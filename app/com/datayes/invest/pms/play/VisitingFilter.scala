package com.datayes.invest.pms.play

import play.api.mvc.{Results, Result, RequestHeader, Filter}
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.config.Config
import scala.collection.JavaConversions._


class VisitingFilter extends Filter with Logging with Results {

  val whitelist = Config.INSTANCE.getStringList("visitor.whitelist", List("10.20.102.*"))

  override def apply(next: (RequestHeader) => Result)(rh: RequestHeader): Result = {
    logger.debug("{} {} from {}", rh.method, rh.path, rh.remoteAddress)

    val remoteAddress = rh.remoteAddress
    val isInList = whitelist.exists(a => checkVisitor(remoteAddress, a))
    val path = rh.path
    val isDebugPath = checkDebugPath(path)
    
    if ( isDebugPath && !isInList ) {
      Forbidden
    }
    else {
      next(rh)
    }
  }

  def checkVisitor(remoteAddress: String, whiteAddress: String): Boolean =  {
    val remotes = remoteAddress.split(".")
    val whites = whiteAddress.split(".")

    var flag = true
    try {
      var i = 0
      for ( s <- remotes ) {
        if ( whites(i) != "*" ) {
          if ( remotes(i) != whites(i) ) {
            flag = false
          }
        }
        i += 1
      }
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => {
        logger.error("The size of remote address {} doesn't match white list address {}", remoteAddress, whiteAddress)
        flag = false
      }
      case _: Throwable => flag = false
    }

    flag
  }


  def checkDebugPath(path: String): Boolean = {
    path.startsWith("""/tools""") || path.startsWith("""/debug""")
  }

}
