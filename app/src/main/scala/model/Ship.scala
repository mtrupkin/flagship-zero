package model

import controller.TieredMotion
import org.mtrupkin.math._
import spriteset.{Oryx, Sprite}

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    faction: String,
    var position: Point,
    var speed: Int = 0,
    var heading: Vect = Vect.Up,
    var energy: Int = 4,
    var power: Int = 3,
    var maxPower: Int = 3,
    maximumSpeed: Int = 25,
    maxShields: Int = 30,
    turnRadius: Int = 2,
    weapons: Seq[Weapon] = Nil) extends Movable with Targetable {

  override def endTurn(): Unit = {
    super.endTurn()
    speed = 0
  }

  def move(p1: Point): Unit = {
    val motion = new TieredMotion(position, heading)
    val v = motion(p1)
    heading = v
    speed = v.normal.toInt
    active = true
    power -= 1
  }


  var shields = maxShields

  override def damage(amount: Int): Unit = {
    shields -= amount
  }

  override def sprite: Sprite = {
    Oryx.sprite(s"${Ship.subtypeMap(subtype)}-${Ship.factionMap(faction)}", 2)
  }
}

object Ship {
  // ship classes
  // military
  // science
  // engineering
  // explorer - science/military
  val subtypeMap = Map(
    "military" -> "ship-5",
    "explorer" -> "ship-4",
    "science" -> "ship-3"
  )

  val factionMap = Map(
    "friendly" -> "blue",
    "enemy" -> "green"
  )
}

