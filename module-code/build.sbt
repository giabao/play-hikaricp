organization := "com.sandinh"

name := "play-hikaricp"

version := "1.6.0"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  "com.zaxxer" % "HikariCP-java6" % "2.2.4",
  "commons-configuration" % "commons-configuration" % "1.10",
  "commons-collections" % "commons-collections" % "3.2.1"
)

scalacOptions := Seq("-feature", "-deprecation")

