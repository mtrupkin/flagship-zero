package org.mtrupkin.control
import javafx.scene.image.ImageView
import javafx.scene.layout.{HBox, Pane, StackPane}

import control.Layer
import org.mtrupkin.math.{Point, Size, Vect}

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait ConsoleFx extends Pane {
  /** position is in world coordinates and is in center of image */
  def draw(p: Point, size: Int, imageView: ImageView): Unit

  /** convert from screen pixels coordinates to world coordinates */
  def screenToWorld(x: Double, y: Double): Point
}

class ConsoleFxImpl(val screenSize: Size = Size(640, 640)) extends ConsoleFx {
  // size of individual tile in pixels
  var tileSize = Size(8, 8)
  val worldSize = Size(screenSize.width / tileSize.width, screenSize.height / tileSize.height)

  setMinSize(screenSize.width, screenSize.height)

  val coordinates = new CoordinateConverter(screenSize, worldSize)

  def draw(p: Point, size: Int, imageView: ImageView): Unit = {
    val screen = worldToScreen(p)
    val adj = tileSize*(size/2)

    drawScreen(screen - adj, imageView)
  }

  def drawScreen(screen: Point, imageView: ImageView): Unit = {
    val box = new StackPane
    box.setStyle("-fx-border-width: 1; -fx-border-color: blue")
    box.relocate(screen.x, screen.y)
    box.getChildren.add(imageView)
//  imageView.relocate(p.x, p.y)
//  getChildren.add(imageView)
    getChildren.add(box)
  }

  def worldToScreen(world: Point): Point = {
    val p = coordinates.toScreen(world)
    val screen = Point(p.x * tileSize.width, p.y * tileSize.height)
    screen
  }

  def screenToWorld(x: Double, y: Double): Point = {
    implicit def toInt(d: Double): Int = { Math.floor(d).toInt }
    val screen = Point(x, y)
    val p = coordinates.toCartesian(screen)

    val world = Point(p.x / tileSize.width, p.y / tileSize.height)
    world
  }
}

class CoordinateConverter(val screenSize: Size, val quadrantSize: Size) {
  val screenSize2 = screenSize / 2
  val (screenWidth2, screenHeight2) = screenSize2
  val scale: Vect = Vect(quadrantSize.width/screenWidth2, quadrantSize.height/screenHeight2)

  // converts coordinates with origin in upper left to
  // coordinates with origin in center
  def toCartesian(p: Point): Point = {
    val p0 = p * scale

    Point(p0.x - screenWidth2, screenHeight2 - p0.y)
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def toScreen(p: Point): Point = Point(p.x + screenWidth2, screenHeight2 - p.y)
}

object ConsoleFx {
  def apply(): ConsoleFx = new ConsoleFxImpl()
}