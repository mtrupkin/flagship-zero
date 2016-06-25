package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Vect(x: Int, y: Int) {
  def +(v: Vect): Vect = Vect(x + v.x, y + v.y)
  def -(v: Vect): Vect = Vect(x - v.x, y - v.y)

  def angleRadians: Double = (Math.PI/2) - Math.atan2(y, x)
}

object Vect {
  val Up = Vect(0, 1)
  val Down = Vect(0, -1)
  val Left = Vect(-1, 0)
  val Right = Vect(1, 0)
}