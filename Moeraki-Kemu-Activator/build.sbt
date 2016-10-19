name := """Moeraki-Kemu-Activator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "moeraki-kemu" % "moeraki-kemu" % "1.0-SNAPSHOT" from "file:C:\\Users\\Eti\\git\\etgramli\\Moeraki_Kemu_Activator\\Moeraki-Kemu-Activator\\moeraki-kemu.jar"

fork in run := true