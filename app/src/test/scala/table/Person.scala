package table

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
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

object Person {
  val characters = ObservableBuffer[Person](
    new Person("Peggy", "Sue", "555-6798"),
    new Person("Rocky", "Raccoon", "555-6798"),
    new Person("Rogger", "Rabbit", "555-6798"),
    new Person("Mickey", "Mouse", "555-6798")
  )
}

