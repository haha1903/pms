package com.datayes.invest.pms.web.servlet

import javax.servlet.ServletContextEvent
import play.api.{Mode, Logger}
import play.core.server.servlet.Play2WarServer

class Play2Servlet25 extends play.core.server.servlet25.Play2Servlet {

  override def contextInitialized(e: ServletContextEvent): Unit = {
    super.contextInitialized(e)
    reconfigureLogger()
  }

  private def reconfigureLogger(): Unit = {
    val validValues = Set("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF", "INHERITED")
    val setLevel = (level: String) => level match {
      case "INHERITED" => null
      case level => ch.qos.logback.classic.Level.toLevel(level)
    }

    val configuration = Play2WarServer.configuration

    Logger.configure(
      levels = configuration.getConfig("logger").map { loggerConfig =>
        loggerConfig.keys.map {
          case "resource" | "file" | "url" => "" -> null
          case key @ "root" => "ROOT" -> loggerConfig.getString(key, Some(validValues)).map(setLevel).get
          case key => key -> loggerConfig.getString(key, Some(validValues)).map(setLevel).get
        }.toMap
      }.getOrElse(Map.empty),
      mode = Mode.Prod)
  }
}
