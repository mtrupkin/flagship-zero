package model

import org.mtrupkin.math.{Point, Vect}

/** Created by mtrupkin on 6/30/2016. */
trait Entity {
  def position: Point
}

case class Star(name: String, subtype: String, position: Point) extends Entity

case class Planet(name: String, subtype: String, position: Point) extends Entity

case class Ship(
  name: String,
  subtype: String,
  position: Point,
  heading: Vect = Vect.Up,
  maxTurn: Double = Math.PI/2,
  maxSpeed: Int = 30) extends Entity {
  def move(velocity: Vect): Ship = {
    val p = position + velocity
    copy(position = p, heading = velocity.normalize)
  }
}

