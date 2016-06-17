package org.mtrupkin.control
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.Pane
import javafx.scene.paint.Color

import org.mtrupkin.cell.CellMap
import org.mtrupkin.console.RGB
import org.mtrupkin.math.Size
import org.mtrupkin.math.{Matrix, Point}

/**
  * Created by mtrupkin on 3/22/2016.
  */
class ConsoleFx(val size: Size = Size(40, 20), val tileSize: Size = Size(8, 8)) extends Pane {
  val imageViews = new Matrix[ImageView](size)
  var cursor: Option[Point] = None

  val (sizeX, sizeY) = cellToPixel(Point(size.width, size.height))
  setMinSize(sizeX, sizeY)

  size.foreach(p => {
    val imageView = new ImageView()
    imageViews(p) = imageView

    val (px, py) = cellToPixel(p)
    imageView.relocate(px + 2, py + 2)
    getChildren.add(imageView)
  })

  def draw(view: CellMap): Unit = {
    def toColor(rgb: RGB): Color = Color.rgb(rgb.r, rgb.g, rgb.b)

    def toHex(rgb: RGB): String = {
      def toHex(x: Int): String = "%02X".format(x)

      val r0 = toHex(rgb.r)
      val g0 = toHex(rgb.g)
      val b0 = toHex(rgb.b)

      s"#$r0$g0$b0"
    }

    def draw(imageView: ImageView, sprite: Image): Unit = {

    }

    size.foreach(p => {
      view(p).map(cell => draw(imageViews(p), cell.char))
    })
  }



  def updateCursor(x: Double, y: Double): Unit = {
    cursor = pixelToCell(x, y)
  }

  private def pixelToCell(x: Double, y: Double): Option[Point] = {
    def floor(d: Double): Int = { Math.floor(d).toInt }

    val (width, height) = tileSize
    val c = Point(floor(x / width), floor(y / height))
    if (size.in(c)) Some(c) else None
  }

  private def cellToPixel(p: Point): (Double, Double) = {
    val (width, height) = tileSize
    (p.x * width, p.y * height)
  }
}