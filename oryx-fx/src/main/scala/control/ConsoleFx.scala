package org.mtrupkin.control

import scalafx.scene.canvas.Canvas
import javafx.scene.layout.{HBox, Pane, StackPane}

import org.mtrupkin.math.{Point, Size, Vect}
import spriteset._

import scalafx.scene.paint.Color

trait ConsoleFx extends Pane {
  /** prepare to draw */
  def clear(): Unit

  def drawSprite(p: Point, sprite: Sprite): Unit

  def drawVect(p: Point, v: Vect): Unit
}

class ConsoleFxImpl(val screen: Size) extends ConsoleFx {
  val canvas = new Canvas(screen.width, screen.height)
  val gc = canvas.graphicsContext2D
  setMinSize(screen.width, screen.height)

  def clear(): Unit = {
    gc.clearRect(0, 0, screen.width, screen.height)
    getChildren.clear()
    getChildren.add(canvas)
  }

  /** position is in screen coordinates and is in center of image */
  def drawSprite(p: Point, sprite: Sprite): Unit = {
    import sprite._
    val adj = size*SPRITE_UNIT_PIXEL/2.0
    val p0 = p - Point(adj, adj)
    imageView.relocate(p0.x, p0.y)
    getChildren.add(imageView)
  }

  /** position and vector is in screen coordinates */
  def drawVect(p: Point, v: Vect): Unit = {
    gc.stroke = Color.Blue
    gc.lineWidth = 4
    val p1 = p + v
    gc.strokeLine(p.x, p.y, p1.x, p1.y)
  }
}

trait ConvertingConsoleFx extends ConsoleFx {
  val converter: CoordinateConverter

  /** position is in world coordinates and is in center of image */
  abstract override def drawSprite(p: Point, sprite: Sprite): Unit = {
    val p0 = converter.toScreen(p)
    super.drawSprite(p0, sprite)
  }

  /** position and vector is in world coordinates */
  abstract override def drawVect(p: Point, v: Vect): Unit = {
    val p0 = converter.toScreen(p)
    val v0 = converter.toScreen(v)
    super.drawVect(p0, v0)
  }
}

object ConsoleFx {
  def apply(screenSize: Size, coordinateConverter: CoordinateConverter): ConsoleFx =
    new ConsoleFxImpl(screenSize) with ConvertingConsoleFx {
      val converter = coordinateConverter
    }
}