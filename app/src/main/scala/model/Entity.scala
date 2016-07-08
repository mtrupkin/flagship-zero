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
  var position: Point,
  var heading: Vect = Vect.Up,
  maxTurn: Double = Math.PI/4,
  maxSpeed: Int = 10) extends Entity

