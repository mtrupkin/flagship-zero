package model

import javafx.scene.image.ImageView

import control.Layer
import org.mtrupkin.math.Point
import tileset.Oryx

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
sealed trait GameObj {
  def position: Point
}

case class Planet(name: String, position: Point, planetClass: String) extends GameObj

case class Star(name: String, position: Point, starClass: String) extends GameObj

case class Ship(name: String, position: Point, shipClass: String) extends GameObj

class World {
  val planet1 = Planet("Earth", Point(50, 50), "M")
  val star1 = Star("Sol", Point(0, 0), "G")
  val ship1 = Ship("Enterprise", Point(0, -50), "explorer")
  val ship2 = Ship("Reliant", Point(-50, 0), "science")
  val ship3 = Ship("Defiant", Point(50, 0), "military")

  val gameObjects: List[GameObj] = List(planet1, star1, ship1, ship2, ship3)

  val background: Seq[Tile] = {
    for {
      i <- 1 to 20
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(100) + 1
      val y = Random.nextInt(100) + 1
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

  def apply(obj: GameObj): Tile = {
    val sprite = obj match {
      case Planet(_, _, planetClass) => planetSprites(planetClass)
      case Star(_, _, starClass) => starSprites(starClass)
      case Ship(_, _, shipClass) => s"${shipSprites(shipClass)}-green"
    }

    Tile(obj.position, Oryx.imageView(sprite))
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
