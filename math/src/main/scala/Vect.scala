package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Vect(x: Int, y: Int) {
  def +(v: Vect): Vect = Vect(x + v.x, y + v.y)
  def -(v: Vect): Vect = Vect(x - v.x, y - v.y)
}

object Vect {
  val Up = Vect(0, -1)
  val Down = Vect(0, 1)
  val Left = Vect(-1, 0)
  val Right = Vect(1, 0)

  val UpperLeft = Vect(-1, -1)
  val UpperRight = Vect(1, -1)
  val LowerLeft = Vect(-1, 1)
  val LowerRight = Vect(1, 1)
}