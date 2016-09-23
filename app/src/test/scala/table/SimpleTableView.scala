package table

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView}



object SimpleTableView extends JFXApp {
  stage = new PrimaryStage {
    title = "Simple Table View"
    scene = new Scene {
      content = new TableView[Person](Person.characters) {
        val col1 =  new TableColumn[Person, String] {
          text = "First Name"
          cellValueFactory = {_.value.firstName}
          prefWidth = 180
        }
        val col2 =  new TableColumn[Person, String] {
          text = "Last Name"
          cellValueFactory = {_.value.lastName}
          prefWidth = 180
        }
        columns ++= List(col1, col2)
      }
    }
  }
}