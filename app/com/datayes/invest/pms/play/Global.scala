package com.datayes.invest.pms.play

import com.datayes.invest.pms.logging.Logging
import play.api.Application
import play.api.mvc.WithFilters
import java.util.Timer
import org.joda.time.LocalDate
import play.api.libs.json.Json
import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.config.RunningMode
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import com.datayes.invest.pms.persist.PersistService

object Global extends WithFilters(new LoggingFilter) with Logging {

  private lazy val runningMode = {
    val sMode = Config.INSTANCE.getString("system.running.mode", RunningMode.NORMAL.toString())
    RunningMode.valueOf(sMode)
  }

  private lazy val schedulerInterval = Config.INSTANCE.getLong("system.scheduler.interval")
  
  private var injector: Injector = null
  
  private var importerInjector: Injector = null

  override def onStart(app: Application): Unit = {
    createInjectors()
  }
  
  private def createInjectors(): Unit = {
    val injectors = SystemInjectors.INSTANCE;
    injector = injectors.getInjector()
    importerInjector = injectors.getImporterInjector()
  }
  
  private var controllerMap = Map[Class[_], Any]()

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    controllerMap get controllerClass match {
      case Some(obj) => obj.asInstanceOf[A]
      case None =>
        synchronized {
          controllerMap get controllerClass match {
            case Some(obj) => obj.asInstanceOf[A]
            case None =>
              logger.info("Initializing controller of type " + controllerClass.toString)
              val controller = injector.getInstance(controllerClass)
              controllerMap += controllerClass -> controller
              controller
          }
        }
    }
  }
}
