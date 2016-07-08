package org.mtrupkin.control

import org.mtrupkin.math.{Point, Size, Vect}


trait CoordinateConverter {
  /** screen to world ratio */
  def scale: Vect

  /** converts coordinates with origin in upper left to
    * coordinates with origin in center
    */
  def toWorld(p: Point): Point

  /** converts coordinates with origin in center to
    * coordinates with origin in upper left
    */
  def toScreen(p: Point): Point
}

class CoordinateConverterImpl(val screen: Size, val quadrant: Size)
  extends CoordinateConverter {
  val screen2 = screen / 2
  import screen2._

  val scale = Vect(width/quadrant.width.toDouble, height/quadrant.height.toDouble)

  // converts coordinates with origin in upper left to
  // coordinates with origin in center
  def toWorld(p: Point): Point = {
    val p0 = Point(p.x - width, height - p.y)
    p0 / scale
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def toScreen(p: Point): Point = {
    val p0 = p * scale
    Point(p0.x + width, height - p0.y)
  }
}

object CoordinateConverter {
  def apply(screen: Size, quadrant: Size): CoordinateConverter =
    new CoordinateConverterImpl(screen, quadrant)
}
