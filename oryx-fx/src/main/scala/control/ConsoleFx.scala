package org.mtrupkin.control
import javafx.scene.image.ImageView
import javafx.scene.layout.{HBox, Pane, StackPane}

import control.Layer
import org.mtrupkin.math.{Point, Size, Vect}

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait ConsoleFx extends Pane {
  /** position is center of image */
  def drawEntity(world: Point, imageView: ImageView, size: Int): Unit
  /** position is upper left corner of image */
//  def drawTile(world: Point, imageView: ImageView): Unit

  def draw(layers: Seq[Layer]): Unit
  def screenToWorld(x: Double, y: Double): Point
}

class ConsoleFxImpl(val worldSize: Size = Size(81, 81)) extends ConsoleFx {
  // size of individual tile
  var tileDim = Size(8, 8)
  val screenSize = Size(worldSize.width * tileDim.width, worldSize.height * tileDim.height)

  setMinSize(screenSize.width, screenSize.height)

  def drawEntity(world: Point, imageView: ImageView, size: Int): Unit = {
    val screen = worldToScreen(world)
    val adj = tileDim*(size/2)

    drawScreen(screen - adj, imageView)
  }

  def drawTile(world: Point, imageView: ImageView): Unit = {
    val screen = worldToScreen(world)
    drawScreen(screen, imageView)
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

  def draw(layers: Seq[Layer]): Unit = {
    getChildren.clear()

    for {
      layer <- layers
    } draw(layer)
  }

  def draw(layer: Layer): Unit = {
    worldSize.foreach(viewPoint => {
      val worldPoint = Coordinates.screenToCartesian(viewPoint, worldSize)
      val screenPoint = worldToScreen(worldPoint)
      for {
        imageView <- layer(worldPoint)
      } drawScreen(screenPoint, imageView)
    })
  }

  def worldToScreen(world: Point): Point = {
    val p = Coordinates.cartesianToScreen(world, worldSize)
    val screen = Point(p.x * tileDim.width, p.y * tileDim.height)
    screen
  }

  def screenToWorld(x: Double, y: Double): Point = {
    implicit def toInt(d: Double): Int = { Math.floor(d).toInt }
    val screen = Point(x, y)
    val p = Coordinates.screenToCartesian(screen, screenSize)

    val world = Point(p.x / tileDim.width, p.y / tileDim.height)
    world
  }
}

object Coordinates {
  // converts coordinates with origin in upper left to
  // coordinates with origin in center
  def screenToCartesian(p: Point, size: Size): Point = {
    val (width, height) = (size.width/2, size.height/2)
    Point(p.x - width, height - p.y)
  }

  // converts coordinates with origin in center to
  // coordinates with origin in upper left
  def cartesianToScreen(p: Point, size: Size): Point = {
    val (width, height) = (size.width/2, size.height/2)
    Point(p.x + width, height - p.y)
  }
}

object ConsoleFx {
  def apply(): ConsoleFx = new ConsoleFxImpl()
}