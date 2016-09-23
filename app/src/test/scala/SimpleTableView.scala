import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.paint.Color

/**
  * Created by mtrupkin on 9/22/2016.
  */
class Person(firstName_ : String, lastName_ : String, phone_ : String, favoriteColor_ : Color = Color.Blue) {

  val firstName = new StringProperty(this, "firstName", firstName_)
  val lastName = new StringProperty(this, "lastName", lastName_)
  val phone = new StringProperty(this, "phone", phone_)
  val favoriteColor = new ObjectProperty(this, "favoriteColor", favoriteColor_)
}

object SimpleTableView extends JFXApp {

  val characters = ObservableBuffer[Person](
    new Person("Peggy", "Sue", "555-6798"),
    new Person("Rocky", "Raccoon", "555-6798")
  )

  stage = new PrimaryStage {
    title = "Simple Table View"
    scene = new Scene {
      content = new TableView[Person](characters) {
        columns ++= List(
          new TableColumn[Person, String] {
            text = "First Name"
            cellValueFactory = {_.value.firstName}
            prefWidth = 180
          },
          new TableColumn[Person, String] {
            text = "Last Name"
            cellValueFactory = {_.value.lastName}
            prefWidth = 180
          }
        )
      }
    }
  }
}