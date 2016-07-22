package controller

import model.Ship
import org.mtrupkin.math._
import Math._

class ShipMovement(ship: Ship) {
  var heading: Vect = ship.heading.normal(1.0)
  protected var speed: Double = 0.0

  def move(): Ship = {
    val velocity: Vect = heading.normal(speed)

    if (velocity.normal > EPSILON) {
      val p = ship.position + velocity
      ship.copy(position = p, heading = velocity.normalize)
    } else {
      ship.copy(heading = heading)
    }
  }

  def rotate(cursor: Vect): Unit = {
    val input = cursor - ship.position
    val normal = input.normal

    if (normal > EPSILON) {
      heading = input.normal(ship.maximumSpeed)
      speed = 0.0
    }
  }

  def move(cursor: Vect): Unit = {
    val input = cursor - ship.position
    val normal = input.normal

    if (normal > EPSILON) {
      speed = min(ship.maximumSpeed, normal)
      heading = input.normal(speed)
    }
  }
}
