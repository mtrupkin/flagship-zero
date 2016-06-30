package model

import org.mtrupkin.math.{Point, Vect}

/** Created by mtrupkin on 6/30/2016. */
trait Entity {
  def position: Point
  def subtype: String
}

trait MovableEntity extends Entity {
  def heading: Vect
  def maneuverRating: Double
  def speed: Int
}

case class Star(name: String, subtype: String, position: Point) extends Entity

case class Planet(name: String, subtype: String, position: Point) extends Entity

case class Ship(
  name: String,
  subtype: String,
  position: Point,
  var heading: Vect = Vect.Up,
  maneuverRating: Double = Math.PI/2,
  speed: Int = 10) extends MovableEntity

