package model

import javafx.scene.image.ImageView

import org.mtrupkin.math.Point
import sprite.{Layer, Oryx}

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait Tile {
  def position: Point
  def sprite: String
}

trait GameTile extends Tile {
  def name: String
}

case class BackgroundTile(position: Point, sprite: String) extends Tile

case class Planet(name: String, position: Point) extends GameTile {
  def sprite: String = "planet-blue"
}

case class Star(name: String, position: Point) extends GameTile {
  def sprite: String = "star"
}

class World {
  val planet1 = Planet("Earth", Point(5, 5))
  val star1 = Star("Sol", Point(48, 48))
  val objects: List[GameTile] = List(planet1, star1)

  val background: Seq[BackgroundTile] = {
    for {
      i <- 1 to 1000
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(100) + 1
      val y = Random.nextInt(100) + 1
      new BackgroundTile(Point(x, y), s"bg-$rnd")
    }
  }

  protected def layer(tiles: Seq[Tile]): Layer = {
    new Layer {
      def apply(p: Point): Option[ImageView] = {
        for {
          tile <- tiles.find(_.position == p)
        } yield Oryx.imageView(tile.sprite)
      }
    }
  }

  def backgroundLayer: Layer = layer(background)
  def objectLayer: Layer = layer(objects)

  def layers: Seq[Layer] = List(backgroundLayer, objectLayer)
}

object WorldBuilder {
  def apply(): World = {
    val world = new World
    world
  }
}
