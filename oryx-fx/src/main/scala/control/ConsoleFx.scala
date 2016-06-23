package org.mtrupkin.control
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

import control.Layer
import org.mtrupkin.math.{Point, Size}

/**
  * Created by mtrupkin on 3/22/2016.
  */
class ConsoleFx(val size: Size = Size(80, 80)) extends Pane {
  val tileSize = 8

  var cursor: Option[Point] = None

  protected val controlSize = tileToPixel(size)
  protected val controlSizeHalf = Size(controlSize.x/2, controlSize.y/2)
  setMinSize(controlSize.x, controlSize.y)

  def worldToView(world: Point): Point = {
    Point(world.x + controlSizeHalf.width, controlSizeHalf.height - world.y)
  }

  def viewToWorld(view: Point): Point = {
    Point(view.x - controlSizeHalf.width, controlSizeHalf.height - view.y)
  }

  def draw(p: Point, imageView: ImageView): Unit = {
    val p0 = worldToView(p)
    imageView.relocate(p0.x, p0.y)
    getChildren.add(imageView)
  }

  def draw(layers: Seq[Layer]): Unit = {
    getChildren.clear()

    for {
      layer <- layers
    } draw(layer)
  }

  protected def draw(layer: Layer): Unit = {
    size.foreach(view => {
      val world = viewToWorld(view)
      for {
        imageView <- layer(world)
        pixel = tileToPixel(view)
      } draw(pixel, imageView)
    })
  }

  def updateCursor(x: Double, y: Double): Unit = {
    cursor = pixelToTile(x, y)
  }

  protected def pixelToTile(x: Double, y: Double): Option[Point] = {
    def floor(d: Double): Int = { Math.floor(d).toInt }

    val c = Point(floor(x / tileSize), floor(y / tileSize))
    if (size.in(c)) Some(c) else None
  }

  protected def tileToPixel(p: Point): Point = {
    Point(p.x * tileSize, p.y * tileSize)
  }
}