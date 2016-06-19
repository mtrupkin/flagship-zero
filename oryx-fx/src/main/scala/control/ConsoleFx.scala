package org.mtrupkin.control
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.Pane
import javafx.scene.paint.Color

import org.mtrupkin.cell.CellMap
import org.mtrupkin.console.RGB
import org.mtrupkin.math.Size
import org.mtrupkin.math.{Matrix, Point}
import sprite.MapLayer

/**
  * Created by mtrupkin on 3/22/2016.
  */
class ConsoleFx(val size: Size = Size(40, 20),
                val tileSize: Size = Size(8, 8)) extends Pane {
  val imageViews = new Matrix[ImageView](size)
  var cursor: Option[Point] = None

  val (sizeX, sizeY) = tileToPixel(Point(size.width, size.height))
  setMinSize(sizeX, sizeY)

  def draw(layer: MapLayer): Unit = {
    getChildren.removeAll()

    size.foreach(p => {
      for {
        imageView <- layer(p)
        (px, py) = tileToPixel(p)
      } {
        imageView.relocate(px + 2, py + 2)
        getChildren.add(imageView)
      }
    })
  }

  def updateCursor(x: Double, y: Double): Unit = {
    cursor = pixelToTile(x, y)
  }

  private def pixelToTile(x: Double, y: Double): Option[Point] = {
    def floor(d: Double): Int = { Math.floor(d).toInt }

    import tileSize._
    val c = Point(floor(x / width), floor(y / height))
    if (size.in(c)) Some(c) else None
  }

  private def tileToPixel(p: Point): (Double, Double) = {
    import tileSize._
    (p.x * width, p.y * height)
  }
}