name := """flagship-zero"""

lazy val math = project

lazy val console = project.in(file("console-lib")).dependsOn(math)

lazy val oryx = project.in(file("oryx-fx")).dependsOn(console)

lazy val app = project.dependsOn(oryx)
