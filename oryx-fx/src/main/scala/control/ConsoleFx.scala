package org.mtrupkin.control

import scalafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.layout.{HBox, Pane, StackPane}

import org.mtrupkin.math.{Point, Size, Vect}

import scalafx.scene.paint.Color

/*
  * Created by mtrupkin on 3/22/2016.
  */
trait ConsoleFx extends Pane {
  /** prepare to draw */
  def clear(): Unit

  /** position is in world coordinates and is in center of image */
  def draw(p: Point, size: Size, imageView: ImageView): Unit

//  def drawSprite(sprite: Tile): Unit

  /** convert from screen pixels coordinates to world coordinates */
  def screenToWorld(x: Double, y: Double): Point

  def drawVect(p: Point, v: Vect): Unit
}

class ConsoleFxImpl(val screen: Size = Size(640, 640)) extends ConsoleFx {
  // size of individual tile in pixels
  var tileSize2 = Size(8, 8)
  val worldSize = Size(screen.width / tileSize2.width, screen.height / tileSize2.height)
  val canvas = new Canvas(screen.width, screen.height)
  val gc = canvas.graphicsContext2D
  setMinSize(screen.width, screen.height)

  val converter = CoordinateConverter(screen, worldSize)
  import converter._

  def clear(): Unit = {
    gc.clearRect(0, 0, screen.width, screen.height)
    getChildren.clear()
    getChildren.add(canvas)
  }

  def drawVect(p: Point, v: Vect): Unit = {
    gc.stroke = Color.Blue
    gc.lineWidth = 4
    val p0 = toScreen(p)
    val p1 = toScreen(p + v)
    gc.strokeLine(p0.x, p0.y, p1.x, p1.y)
  }

  def draw(p: Point, size: Size, imageView: ImageView): Unit = {
    val screen = toScreen(p)
    val adj = size*scale

    drawScreen(screen - adj, imageView)
  }

  def drawScreen(p: Point, imageView: ImageView): Unit = {
    imageView.relocate(p.x, p.y)
    getChildren.add(imageView)
  }

  def screenToWorld(x: Double, y: Double): Point = toWorld(Point(x, y))
}

object ConsoleFx {
  def apply(): ConsoleFx = new ConsoleFxImpl()
}