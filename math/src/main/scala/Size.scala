package org.mtrupkin.math



/**
  * Created by mtrupkin on 3/22/2016.
  */
case class Size(width: Int, height: Int) {
  def foreach(f: (Point => Unit)): Unit = {
    for {
      x <- 0 until width
      y <- 0 until height
    } f(Point(x, y))
  }

  def *(scale: Int): Size = Size(width * scale, height * scale)
  def /(scale: Int): Size = Size(width / scale, height / scale)
  def in(p: Point): Boolean = (p.x >= 0 && p.y >= 0 && p.x < width && p.y < height)

  import Math._
  def diagonal: Double = sqrt(width*width + height*height)
}

object Size {
  val ZERO = new Size(0, 0)
  val ONE = new Size(1, 1)

  // conversions
  implicit def toTuple(s: Size): (Int, Int) = (s.width, s.height)
  implicit def fromTuple(t: (Int, Int)): Size = Size(t._1, t._2)

  implicit def toVect(s: Size): Vect = Vect(s.width, s.height)
  implicit def toPoint(s: Size): Point = Point(s.width, s.height)
}