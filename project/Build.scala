import sbt._
import Keys._
import play.Project._
import com.github.play2war.plugin.{ Play2WarKeys, Play2WarPlugin }

object ApplicationBuild extends Build {

  val appName         = "pms"
  val appVersion      = "SNAPSHOT"

  val hibernateVersion = "4.2.6.Final"
  val protostuffVersion = "1.0.7"
    
  val artifactoryUrl = "http://artifactory.datayes.com/artifactory"

  val appDependencies = Seq(
    "com.dyuproject.protostuff" % "protostuff-core" % protostuffVersion,
    "com.dyuproject.protostuff" % "protostuff-runtime" % protostuffVersion,
    "com.datayes.paas.sso" % "datayes-sso" % "1.1.1" withSources,
    "com.google.code.gson" % "gson" % "2.2.4",
    "com.google.inject" % "guice" % "3.0",
    "com.rabbitmq" % "amqp-client" % "3.1.4",
    "commons-codec" % "commons-codec" % "1.8",
    "joda-time" % "joda-time" % "2.2",
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.apache.activemq" % "activemq-client" % "5.8.0",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "org.apache.geronimo.specs" % "geronimo-servlet_2.5_spec" % "1.2",
    "org.hibernate" % "hibernate-c3p0" % hibernateVersion,
    "org.hibernate" % "hibernate-ehcache" % hibernateVersion,
    "org.hibernate" % "hibernate-entitymanager" % hibernateVersion,
    "org.slf4j" % "slf4j-api" % "1.7.2",
    "redis.clients" % "jedis" % "2.1.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    net.virtualvoid.sbt.graph.Plugin.graphSettings: _*
  ).settings(
    Play2WarPlugin.play2WarSettings: _*
  ).settings(
    Play2WarKeys.servletVersion := "2.5",
    Play2WarKeys.targetName := Some(appName)
  ).settings(
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    resolvers += "datayes" at artifactoryUrl + "/libs-release",
    publishTo := Some("dist" at artifactoryUrl + "/invest-platform-release")
  )
}
