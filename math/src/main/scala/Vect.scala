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
  def toVect(p0: Point, p1: Point): Vect = Vect(p1.x - p0.x, p1.y - p0.y)

  val Up = Vect(0, 1)
  val Down = Vect(0, -1)
  val Left = Vect(-1, 0)
  val Right = Vect(1, 0)
}