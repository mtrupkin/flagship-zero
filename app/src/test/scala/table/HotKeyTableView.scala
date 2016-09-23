package table

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Scene, input}
import scalafx.scene.control.{SelectionMode, TableColumn, TableView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scala.collection._



object HotKeyTableView extends JFXApp {
  val hotkeys: mutable.Map[KeyCode, Seq[Int]] = mutable.Map()

  val table = new TableView[Person](Person.characters) {
    val col1 = new TableColumn[Person, String] {
      text = "First Name"
      cellValueFactory = {
        _.value.firstName
      }
      prefWidth = 180
    }
    val col2 = new TableColumn[Person, String] {
      text = "Last Name"
      cellValueFactory = {
        _.value.lastName
      }
      prefWidth = 180
    }
    columns ++= List(col1, col2)

    selectionModel().selectionMode = SelectionMode.Multiple
  }

  stage = new PrimaryStage {
    title = "Hotkey Table View"
    scene = new Scene {
      content = table
      filterEvent(KeyEvent.KeyPressed) { (event: KeyEvent) => keypressed(event) }
    }

  }

  def keypressed(event: KeyEvent): Unit = {
    val code = event.code

    if (code.isDigitKey) {
      if (event.controlDown) {
        val idxs = table.selectionModel.value.getSelectedIndices
        hotkeys(code) = idxs.toSeq.map(_.toInt)
      } else {
        val hotIdxs = hotkeys(code)
        val idxs = table.selectionModel()///value.getSelectedIndices
        idxs.clearSelection()
        hotIdxs.foreach(idxs.select(_))
      }
    }
  }
}