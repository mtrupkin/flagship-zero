name := "app"

version := "1.0"

scalaVersion := "2.11.8"

licenses += ("MIT", url("http://www.opensource.org/licenses/mit-license.html"))

fork in run := true

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scalafx" %% "scalafx" % "8.0.92-R10"
)
