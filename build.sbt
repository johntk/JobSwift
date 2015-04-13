name := """recruit"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies += filters

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.json" % "json" % "20090211",
  "com.google.code.gson" % "gson" % "1.7.1"
)
