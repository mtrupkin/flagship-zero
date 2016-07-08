package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Vect(x: Double, y: Double) {
  def +(v: Vect): Vect = Vect(x + v.x, y + v.y)
  def -(v: Vect): Vect = Vect(x - v.x, y - v.y)

  def *(scale: Double): Vect = Vect(x * scale, y * scale)
  def /(scale: Double): Vect = Vect(x / scale, y / scale)
  def normal(length: Double): Vect = (this/this.normal) * length

  def unary_- = Vect(-x, -y)

  import Math._
  def angle: Double = atan2(y, x)
  def normal: Double = sqrt(x*x + y*y)
  def rotate(theta: Double): Vect = {
    val x0 = x * cos(theta) - y * sin(theta)
    val y0 = x * sin(theta) + y * cos(theta)
    Vect(x0, y0)
  }

  def dot(v: Vect): Double = x * v.x + y * v.y
  def angle(v: Vect): Double = acos(this.dot(v) / (this.normal * v.normal))
}

object Vect {
  val Up = Vect(0, 1)
  val Down = Vect(0, -1)
  val Left = Vect(-1, 0)
  val Right = Vect(1, 0)

  // conversions
  implicit def toTuple(v: Vect): (Double, Double) = (v.x, v.y)
  implicit def fromTuple(t: (Double, Double)): Vect = Vect(t._1, t._2)
  implicit def toPoint(v: Vect): Point = Point(v.x, v.y)
}