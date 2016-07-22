package model

import org.mtrupkin.math.Point
import spriteset.{Oryx, Sprite}

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait World {
  def background: Seq[(Point, Sprite)]
  var entities: Seq[Entity]
  var ship: Ship
}

class WorldImpl extends World {
  val planet1 = Planet("Earth", "M", Point(0, 0))
  val star1 = Star("Sol", "G", Point(-30, -30))
  val ship1a = Ship("Enterprise", "explorer", Point(0, 0))
  val ship2 = Ship("Reliant", "science", Point(0, -15))
  val ship3 = Ship("Defiant", "military", Point(30, -30))
  val reliant = Ship("Reliant", "science", Point(35, 35))

  var ship = ship1a

  var entities: Seq[Entity] = List(reliant)

  val background: Seq[(Point, Sprite)] = {
    for {
      i <- 1 to 5
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(80) - 40
      val y = Random.nextInt(80) - 40
      (Point(x, y), Oryx.sprite(s"bg-$rnd"))
    }
  }
}


object Sprite {
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

  def apply(obj: Entity): Sprite = {
    def toDegrees(radians: Double): Double = -radians*180/Math.PI

    val (name, scale, rotate) = obj match {
      case Planet(_, subtype, _) => (s"${planetSprites(subtype)}", 1, None)
      case Star(_, subtype, _) => (s"${starSprites(subtype)}", 1, None)
      case Ship(_, subtype, _, heading, _, _, _) => {
        (s"${shipSprites(subtype)}-gray", 2, Some(toDegrees(heading.unsignedAngle)))
      }
    }

    Oryx.sprite(name, scale, rotate)
  }
}

object World {
  def apply(): World = {
    val world = new WorldImpl
    world
  }
}
