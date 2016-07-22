package model

import org.mtrupkin.math._

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    position: Point,
    heading: Vect = Vect.Up,
    maximumTurn: Double = Math.PI/4,
    maneuverSpeed: Int = 10,
    maximumSpeed: Int = 30) extends Entity

