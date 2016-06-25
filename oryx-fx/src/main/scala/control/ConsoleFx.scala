package org.mtrupkin.control
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

import control.Layer
import org.mtrupkin.math.{Point, Size}

/**
  * Created by mtrupkin on 3/22/2016.
  */
class ConsoleFx(val tileSize: Size = Size(80, 80)) extends Pane {
  // size of individual tile
  var tileDim = Size(8, 8)

  var cursor: Option[Point] = None

  protected val controlSize = tileToPixel(tileSize)
  // protected val controlSizeHalf = Size(controlSize.x/2, controlSize.y/2)
  protected val tileSizeHalf = Size(tileSize.x/2, tileSize.y/2)

  setMinSize(controlSize.x, controlSize.y)

  def worldToView(world: Point): Point = {
    val p = Point(world.x + tileSizeHalf.width, tileSizeHalf.height - world.y)
    p
  }

  def viewToWorld(view: Point): Point = {
    val p = Point(view.x - tileSizeHalf.width, tileSizeHalf.height - view.y)
    p
  }

  def drawWorld(worldPoint: Point, imageView: ImageView): Unit = {
    val viewPoint = worldToView(worldPoint)
    drawView(viewPoint, imageView)
  }

  def drawView(tilePoint: Point, imageView: ImageView): Unit = {
    val p = tileToPixel(tilePoint)
    imageView.relocate(p.x, p.y)
    getChildren.add(imageView)
  }

  def draw(layers: Seq[Layer]): Unit = {
    getChildren.clear()

    for {
      layer <- layers
    } draw(layer)
  }

  protected def draw(layer: Layer): Unit = {
    tileSize.foreach(viewPoint => {
      val worldPoint = viewToWorld(viewPoint)
      for {
        imageView <- layer(worldPoint)
      } drawView(viewPoint, imageView)
    })
  }

  def updateCursor(x: Double, y: Double): Unit = {
    cursor = pixelToTile(x, y)
  }

  protected def pixelToTile(x: Double, y: Double): Option[Point] = {
    def floor(d: Double): Int = { Math.floor(d).toInt }

    val c = Point(floor(x / tileDim.x), floor(y / tileDim.y))
    if (tileSize.in(c)) Some(c) else None
  }

  protected def tileToPixel(p: Point): Point = {
    Point(p.x * tileDim.x, p.y * tileDim.y)
  }
}