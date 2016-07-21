package controller

import model.Ship
import org.mtrupkin.math._

class ShipMovement(ship: Ship) {
  protected var heading: Vect = ship.heading
  protected var speed: Double = 0.0

  def velocity: Vect = heading.normal(speed)

  def move(cursor: Vect): Unit = {
    val input = cursor - ship.position
    speed = input.normal
    if (speed > 0) {
      var angle = ship.heading.signedAngle(input)
      angle = limit(angle, -ship.maxTurn, ship.maxTurn)
      heading = ship.heading.rotate(angle)
    }
    speed = limit(input.normal, ship.minSpeed, ship.maxSpeed)
  }
}
