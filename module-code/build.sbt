organization := "com.sandinh"

name := "play-hikaricp"

version := "1.7.1"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  "com.zaxxer" % "HikariCP-java6" % "2.2.5"
)

scalacOptions := Seq("-feature", "-deprecation")

