// Comment to get more information during initialization
logLevel := Level.Info

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.3")

// play2war
addSbtPlugin("com.github.play2war" % "play2-war-plugin" % "1.0")

// Dependency graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")
