package model

import org.mtrupkin.control.CoordinateConverter
import org.mtrupkin.math.{Point, Size, Vect}
import spriteset._

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait World {
  var entities: Seq[Entity]
  var ship: Ship
  def target(p: Point): Option[Target]
  def converter: CoordinateConverter
}

class WorldImpl() extends World {
  val screenSize = Size(720, 720)
  // world unit size is one sprite unit
  // world size (90, 90)
  val worldSize = screenSize / SPRITE_UNIT_PIXEL
  val worldSize2 = worldSize / 2

  val converter = CoordinateConverter(screenSize, worldSize)

  import Weapon._

  val planet1 = Planet("Earth", "M", Point(0, 0))
  val star1 = Star("Sol", "G", Point(-30, -30))
  val ship3 = Ship("Defiant", "military", "friendly",Point(30, -30))
  val reliant = Ship("Reliant", "science", "enemy", Point(30, 0))

  var ship = Ship("Enterprise", "explorer", "friendly", Point(0, -35), weapons = List(Phaser1))

  def target(p: Point): Option[Target] = {
    def accept(e: Target): Boolean = {
      val pixels = e.sprite.size * SPRITE_UNIT_PIXEL / 2
      val v = Vect(pixels, pixels)
      val q = converter.toWorld(v)
      val n = (p - e.position)
      n.normal < q.normal
    }

    entities.collectFirst {
      case e: Target if accept(e) => e
    }
  }

  val background: Seq[Entity] = {
    for {
      i <- 1 to 5
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(worldSize.width) - worldSize2.width
      val y = Random.nextInt(worldSize.height) - worldSize2.height
      StaticEntity(Point(x, y), Oryx.sprite(s"bg-$rnd"))
    }
  }

  var entities: Seq[Entity] = List(reliant, star1,  planet1) ++ background

}

object World {
  def apply(): World = {
    val world = new WorldImpl()
    world
  }
}
