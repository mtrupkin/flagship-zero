package org.mtrupkin.console

case class RGB(r: Int, g: Int, b:Int)

object RGB {
  val Black = RGB(0, 0, 0)
  val White = RGB(255, 255, 255)
  val LightGrey = RGB(126, 126, 126)
  val Yellow = RGB(255, 255, 0)
  val Blue = RGB(0, 0, 255)
  val Red = RGB(255, 0, 0)
  val Green = RGB(0, 255, 0)
  val LightYellow = RGB(126, 126, 0)
  val LightBlue = RGB(21, 105, 199)
  val LightRed = RGB(126, 0, 0)
  val LightGreen = RGB(0, 126, 0)
}
