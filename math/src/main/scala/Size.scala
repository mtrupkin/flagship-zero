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

  def in(p: Point): Boolean = (p.x >= 0 && p.y >= 0 && p.x < width && p.y < height)
}

object Size {
  val ZERO = new Size(0, 0)
  val ONE = new Size(1, 1)

  // conversions
  implicit def ToPoint(s: Size): Point = Point(s.width, s.height)
}