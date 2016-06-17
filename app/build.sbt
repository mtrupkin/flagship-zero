name := "app"

version := "1.0"

scalaVersion := "2.11.8"

licenses += ("MIT", url("http://www.opensource.org/licenses/mit-license.html"))

fork in run := true

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.60-R9"
)
