package org.mtrupkin.control
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

import org.mtrupkin.math.{Matrix, Point, Size}
import sprite.Layer

/**
  * Created by mtrupkin on 3/22/2016.
  */
class ConsoleFx(val size: Size = Size(100, 100)) extends Pane {
  val tileSize = 8

  val imageViews = new Matrix[ImageView](size)
  var cursor: Option[Point] = None

  val (sizeX, sizeY) = tileToPixel(Point(size.width, size.height))
  setMinSize(sizeX, sizeY)


  def draw(layers: Seq[Layer]): Unit = {
    def draw(layer: Layer): Unit = {
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

    getChildren.clear()

    for {
      layer <- layers
    } draw(layer)

  }
  def updateCursor(x: Double, y: Double): Unit = {
    cursor = pixelToTile(x, y)
  }

  private def pixelToTile(x: Double, y: Double): Option[Point] = {
    def floor(d: Double): Int = { Math.floor(d).toInt }

    val c = Point(floor(x / tileSize), floor(y / tileSize))
    if (size.in(c)) Some(c) else None
  }

  private def tileToPixel(p: Point): (Double, Double) = {
    (p.x * tileSize, p.y * tileSize)
  }
}