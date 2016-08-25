name := "console-lib"

organization := "org.mtrupkin"

licenses += ("MIT", url("http://www.opensource.org/licenses/mit-license.html"))

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
   "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
