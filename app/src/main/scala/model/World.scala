package model

import javafx.scene.image.ImageView

import control.Layer
import org.mtrupkin.math.{Point, Vect}
import tileset.Oryx

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */

class World {
  val planet1 = Planet("Earth", "M", Point(0, 0))
  val star1 = Star("Sol", "G", Point(-30, -30))
  val ship1 = Ship("Enterprise", "explorer", Point(-30, -30))
  val ship2 = Ship("Reliant", "science", Point(0, -15))
  val ship3 = Ship("Defiant", "military", Point(30, -30))

  val gameObjects: List[Entity] = List(planet1, ship1)

  val background: Seq[Tile] = {
    for {
      i <- 1 to 5
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(80) - 40
      val y = Random.nextInt(80) - 40
      Tile(Point(x, y), Oryx.imageView(s"bg-$rnd"))
    }
  }

  def objects: Seq[Tile] = {
    gameObjects.map(obj => Tile(obj))
  }

  def layers: Seq[Layer] = List(background).map(Tile.toLayer(_))
}

case class Tile(position: Point, imageView: ImageView)

object Tile {
  // M earth-like
  // A aquatic
  // B barren
  // D desert
  // F frozen
  // G gas giant
  // P poisonous
  val planetSprites = Map(
    "M" -> "planet-blue",
    "A" -> "planet-blue-rings",
    "B" -> "planet-gray",
    "D" -> "planet-rock",
    "F" -> "planet-gray-exploded",
    "G" -> "planet-blue-large",
    "P" -> "planet-purple"
  )

  // star classes
  // O hot
  // B hot super-giant
  // A dwarf star
  // F
  // G sol-like
  // K cool giant
  // M cool super-giant / dwarf
  val starSprites = Map(
    "O" -> "star",
    "B" -> "star",
    "A" -> "star",
    "F" -> "star",
    "G" -> "star",
    "K" -> "star",
    "M" -> "star"
  )

  // ship classes
  // military
  // science
  // engineering
  // explorer - science/military
  val shipSprites = Map(
    "military" -> "ship-5",
    "explorer" -> "ship-1",
    "science" -> "ship-3"
  )

  def apply(obj: Entity): Tile = {
    var rotate: Option[Int] = None
    val (sprite, scale) = obj match {
      case Planet(_, subtype, _) => s"${planetSprites(subtype)}" -> 2
      case Star(_, subtype, _) => s"${starSprites(subtype)}" -> 1
      case Ship(_, subtype, _, heading, _, _) => {
        rotate = Some((heading.angleRadians*180/Math.PI).toInt)
        s"${shipSprites(subtype)}-gray" -> 2
      }
    }

    val imageView = Oryx.imageView(sprite, scale)
    rotate.map(angle=>imageView.setRotate(angle))

    Tile(obj.position, imageView)
  }

  def toLayer(tiles: Seq[Tile]): Layer = {
    new Layer {
      def apply(p: Point): Option[ImageView] = {
        for {
          tile <- tiles.find(_.position == p)
        } yield tile.imageView
      }
    }
  }
}

object WorldBuilder {
  def apply(): World = {
    val world = new World
    world
  }
}
