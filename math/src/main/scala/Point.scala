package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Point(x: Double, y: Double) {
  def +(v: Vect): Point = Point(x + v.x, y + v.y)
  def -(v: Vect): Point = Point(x - v.x, y - v.y)
  def *(v: Vect): Point = Point(x * v.x, y * v.y)
  def /(v: Vect): Point = Point(x / v.x, y / v.y)
  def -(p: Point): Vect = Vect(x - p.x, y - p.y)

  def neighbors(r: Int = 1): Seq[Point] = {
    for {
      x0 <- -r to r
      y0 <- -r to r
      if !((x0 == 0) && (y0 == 0))
    } yield Point(x + x0, y + y0)
  }
}

object Point {
  val Origin: Point = Point(0, 0)
  def polar(r: Double, theta: Double) = Point(r*Math.cos(theta), r*Math.sin(theta))

  // conversions
  implicit def toTuple(p: Point): (Double, Double) = (p.x, p.y)
  implicit def fromTuple(t: (Double, Double)): Point = Point(t._1, t._2)


  implicit def toSize(p: Point): Size = Size(p.x.toInt, p.y.toInt)
  implicit def toVect(p: Point): Vect = Vect(p.x, p.y)
}
