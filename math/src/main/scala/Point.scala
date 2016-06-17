package org.mtrupkin.math

/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Point(x: Int, y: Int) {
  def +(v: Vect): Point = Point(x + v.x, y + v.y)
  def -(v: Vect): Point = Point(x - v.x, y - v.y)

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
}
