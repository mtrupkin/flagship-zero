package controller

import model.Ship
import org.mtrupkin.math.Vect

class ShipMovement(ship: Ship) {
  var heading: Vect = ship.heading

  def move(cursor: Vect): Unit = {
    val input = cursor - ship.position
    val angle = ship.heading.angle(input)
    if (angle <= ship.maxTurn) {
      heading = input
      val normal = input.normal
      if (normal > ship.maxSpeed) {
        heading = heading.normal(ship.maxSpeed)
      }
    }
  }
}
