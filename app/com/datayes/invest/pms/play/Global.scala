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
import com.datayes.invest.pms.system.SystemInjectors;
import com.datayes.invest.platform.system.SystemScheduler

object Global extends WithFilters(new LoggingFilter) with Logging {

  private lazy val runningMode = {
    val sMode = Config.INSTANCE.getString("system.running.mode", RunningMode.NORMAL.toString())
    RunningMode.valueOf(sMode)
  }

  private lazy val schedulerInterval = Config.INSTANCE.getLong("system.scheduler.interval")
  
  private var injector: Injector = null


  override def onStart(app: Application): Unit = {
    createInjectors()
    initializeSystem()
  }
  
  private def initializeSystem() {
    if (runningMode != RunningMode.NO_DAEMON) {
      val scheduler = injector.getInstance(classOf[SystemScheduler])
      val thread = new Thread(scheduler)
      thread.start()
    }
  }
  
  private def createInjectors(): Unit = {
    val injectors = SystemInjectors.INSTANCE;
    injector = injectors.getInjector()
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
