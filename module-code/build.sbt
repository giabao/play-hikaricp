organization := "com.sandinh"

name := "play-hikaricp"

version := "1.7.2"

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.10.5", "2.11.4")

enablePlugins(PlayScala)

libraryDependencies ++= Seq(jdbc,
  "com.zaxxer" % "HikariCP-java6" % "2.2.5"
)

scalacOptions := Seq("-feature", "-deprecation")

