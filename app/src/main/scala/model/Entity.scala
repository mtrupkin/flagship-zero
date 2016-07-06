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
  var heading: Vect = Vect.Up,
  maxTurn: Double = Math.PI/4,
  speed: Int = 10) extends Entity {

  val initialHeading = heading

  def clipHeading(heading0: Vect): Unit = {
    val angle = initialHeading.angle(heading0)
    println(s"heading: $heading heading0: $heading0 angle: $angle")
    println(s"heading-angle: ${heading.angle} heading0-angle: ${heading0.angle}")
    println()

    if (angle <= maxTurn) {
      heading = heading0
    }
  }
}