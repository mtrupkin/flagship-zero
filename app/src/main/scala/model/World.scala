package model

import javafx.scene.image.ImageView

import control.Layer
import org.mtrupkin.math.{Point, Vect}
import tileset.Oryx

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait World {
  var ship: Ship
}


class WorldImpl extends World {
  val planet1 = Planet("Earth", "M", Point(0, 0))
  val star1 = Star("Sol", "G", Point(-30, -30))
  val ship1a = Ship("Enterprise", "explorer", Point(-30, -30))
  val ship2 = Ship("Reliant", "science", Point(0, -15))
  val ship3 = Ship("Defiant", "military", Point(30, -30))

  var ship = ship1a

  val gameObjects: List[Entity] = List(ship1a)

  val background: Seq[Tile] = {
    val q = for {
      i <- 1 to 5
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(80) - 40
      val y = Random.nextInt(80) - 40
      Tile(Point(x, y), Oryx.imageView(s"bg-$rnd"), 1)
    }
    val e = 40
    q ++ List(
      Tile(Point(-e, e), Oryx.imageView(s"bg-1"),1),
      Tile(Point(e, e), Oryx.imageView(s"bg-1"),1),
      Tile(Point(-e, -e), Oryx.imageView(s"bg-1"),1),
      Tile(Point(e, -e), Oryx.imageView(s"bg-1"),1)
    )
  }

  def objects: Seq[Tile] = {
    gameObjects.map(obj => Tile(obj))
  }
}

case class Tile(position: Point, imageView: ImageView, size: Int)

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
    "explorer" -> "ship-4",
    "science" -> "ship-3"
  )

  def apply(obj: Entity): Tile = {
    def toDegrees(radians: Double): Double = -radians*180/Math.PI

    var rotate: Option[Double] = None
    val (sprite, imageScale, tileScale) = obj match {
      case Planet(_, subtype, _) => (s"${planetSprites(subtype)}", 1, 3)
      case Star(_, subtype, _) => (s"${starSprites(subtype)}", 1, 3)
      case Ship(_, subtype, _, heading, _, _) => {
        rotate = Some(toDegrees(heading.angle))
        (s"${shipSprites(subtype)}-gray", 2, 6)
      }
    }

    val imageView = Oryx.imageView(sprite, imageScale)
    rotate.map(angle=>imageView.setRotate(angle))

    Tile(obj.position, imageView, tileScale)
  }
}

object World {
  def apply(): World = {
    val world = new WorldImpl
    world
  }
}
