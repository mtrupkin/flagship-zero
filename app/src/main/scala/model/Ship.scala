package model

import org.mtrupkin.math.{Point, Vect}

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    position: Point,
    heading: Vect = Vect.Up,
    maxTurn: Double = Math.PI/4,
    minSpeed: Int = 10,
    maxSpeed: Int = 30) extends Entity {

  def move(velocity: Vect): Ship = {
    val p = position + velocity
    copy(position = p, heading = velocity.normalize)
  }
}


