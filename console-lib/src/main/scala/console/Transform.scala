package console

import org.mtrupkin.math.{Point, Size, Vect}

/**
  * Created by mtrupkin on 8/23/2016.
  */
trait Transform {
  var origin: Point

  def screenSize: Size
  def worldSize: Size

  /** converts coordinates with origin in upper left to
    * coordinates with origin in center
    */
  def world(p: Point): Point
  def world(v: Vect): Vect

  /** converts coordinates with origin in center to
    * coordinates with origin in upper left
    */
  def screen(p: Point): Point
  def screen(v: Vect): Vect
  def screen(size: Size): Size
}

class TransformImpl(val screenSize: Size, val worldSize: Size)
  extends Transform {
  var origin = Point.Origin

  val quadrant: Size = worldSize / 2
  val screen2 = screenSize / 2

  import screen2._

  // screen to world ratio
  val scale = Vect(width/quadrant.width.toDouble, height/quadrant.height.toDouble)


  // converts coordinates with origin in upper left to
  // coordinates with origin in center
  def world(p: Point): Point = {
    val p0 = Point(p.x - width, height - p.y)
    (p0 / scale) + origin
  }

  def world(v: Vect): Vect = {
    val v0 = v / scale
    val v1 = Vect(v0.x, -v0.y)
    v1
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def screen(p: Point): Point = {
    val p0 = (p - origin) * scale

    Point(p0.x + width, height - p0.y)
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def screen(v: Vect): Vect = {
    val v0 = v * scale
    val v1 = Vect(v0.x, -v0.y)
    v1
  }

  def screen(s: Size): Size = {
    val v0 = s * scale
    val v1 = Size(v0.x.toInt, -v0.y.toInt)
    v1
  }
}

object Transform {
  def apply(screen: Size, quadrant: Size): Transform =
    new TransformImpl(screen, quadrant)
}
