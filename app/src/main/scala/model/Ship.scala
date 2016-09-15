package model

import org.mtrupkin.math._
import spriteset.{Oryx, Sprite}

trait Weapon {
  def name: String
  def rating: Int
  def attack(range: Double): Int
}

class Phaser1(val rating: Int) extends Weapon {
  val name = "Phaser-1"
  val killZone = 30

  def attack(range: Double): Int = {
    val hits = Combat.attack(rating, 2)
    val damage = range match {
      case x if (x <= killZone) => hits * 2
      case _ => hits
    }
    println(s"damage: $damage")
    damage
  }
}

object Weapon {
  val Phaser1a = new Phaser1(2)
}

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    faction: String,
    var position: Point,
    var heading: Vect = Vect.Up,
    maximumSpeed: Int = 25,
    maxShields: Int = 30,
    turnRadius: Int = 2,
    weapons: Seq[Weapon] = Nil) extends Target {

  var shields = maxShields

  def damage(amount: Int): Unit = {
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

