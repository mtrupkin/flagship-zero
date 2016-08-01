package org.mtrupkin.control

import org.mtrupkin.math.{Point, Size, Vect}


trait CoordinateConverter {
  var origin: Point

  /** converts coordinates with origin in upper left to
    * coordinates with origin in center
    */
  def toWorld(p: Point): Point

  /** converts coordinates with origin in center to
    * coordinates with origin in upper left
    */
  def toScreen(p: Point): Point

  def toScreen(v: Vect): Vect
}

class CoordinateConverterImpl(var origin: Point, val screen: Size, val world: Size)
  extends CoordinateConverter {

  val quadrant: Size = world / 2
  val screen2 = screen / 2

  import screen2._

  // screen to world ratio
  val scale = Vect(width/quadrant.width.toDouble, height/quadrant.height.toDouble)

  // converts coordinates with origin in upper left to
  // coordinates with origin in center
  def toWorld(p: Point): Point = {
    val p0 = Point(p.x - width, height - p.y)
    (p0 / scale) + origin
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def toScreen(p: Point): Point = {
    val p0 = (p - origin) * scale

    Point(p0.x + width, height - p0.y)
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def toScreen(v: Vect): Vect = {
    val v0 = v * scale
    val v1 = Vect(v0.x, -v0.y)
    v1
  }

}

object CoordinateConverter {
  def apply(origin: Point, screen: Size, quadrant: Size): CoordinateConverter =
    new CoordinateConverterImpl(origin, screen, quadrant)
}
