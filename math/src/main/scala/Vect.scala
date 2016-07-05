package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Vect(x: Int, y: Int) {
  def +(v: Vect): Vect = Vect(x + v.x, y + v.y)
  def -(v: Vect): Vect = Vect(x - v.x, y - v.y)

  def *(v: Vect): Point = Vect(x - v.x, y - v.y)
  def unary_- = Vect(-x, -y)


  import Math._
  def angleRadians: Double = (PI/2) - atan2(y, x)
  def normal: Double = sqrt(x*x + y*y)
}

object Vect {
  val Up = Vect(0, 1)
  val Down = Vect(0, -1)
  val Left = Vect(-1, 0)
  val Right = Vect(1, 0)

  // conversions
  implicit def toTuple(v: Vect): (Int, Int) = (v.x, v.y)
  implicit def fromTuple(t: (Int, Int)): Vect = Vect(t._1, t._2)
  implicit def toPoint(v: Vect): Point = Point(v.x, v.y)
}