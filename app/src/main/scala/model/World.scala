package model

import org.mtrupkin.math.{Point, Size}
import spriteset.{Oryx, Sprite}

import scala.util.Random

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait World {
  var entities: Seq[Entity]
  var ship: Ship
  def entity(p: Point): Option[Entity]
}

class WorldImpl() extends World {
  val size = Size(90, 90)
  val size2 = size / 2

  val planet1 = Planet("Earth", "M", Point(0, 0))
  val star1 = Star("Sol", "G", Point(-30, -30))
  val ship1 = Ship("Enterprise", "explorer", "friendly", Point(0, -35))
  val ship3 = Ship("Defiant", "military", "friendly",Point(30, -30))
  val reliant = Ship("Reliant", "science", "enemy", Point(30, 30))

  var ship = ship1

  def entity(p: Point): Option[Entity] = ???

  val background: Seq[Entity] = {
    for {
      i <- 1 to 5
      rnd = Random.nextInt(6) + 1
    } yield {
      val x = Random.nextInt(size.width) - size2.width
      val y = Random.nextInt(size.height) - size2.height
      StaticEntity(Point(x, y), Oryx.sprite(s"bg-$rnd"))
    }
  }

  var entities: Seq[Entity] = List(reliant) ++ background

}

object World {
  def apply(): World = {
    val world = new WorldImpl()
    world
  }
}
