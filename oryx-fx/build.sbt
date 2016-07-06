name := "oryx-fx"

organization := "org.mtrupkin"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.5",
  "org.scalafx" %% "scalafx" % "8.0.92-R10",
  "com.typesafe.play" %% "play-json" % "2.5.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

