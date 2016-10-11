package model

import org.mtrupkin.math._
import spriteset.{Oryx, Sprite}

import scalafx.beans.property.{ObjectProperty, StringProperty}

trait Weapon {
  def name: String
  def rating: Int
  def attack(range: Double): Int

  val nameProp = new StringProperty(this, "name", name)
  val ratingProp = new ObjectProperty(this, "rating", rating)
}

case class Phaser1(rating: Int) extends Weapon {
  lazy val name = "Phaser-1"
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

case class Torpedo1(rating: Int) extends Weapon {
  lazy val name = "Torpedo-1"
  def attack(range: Double): Int = {
    val damage = Combat.attack(rating, 2)
    println(s"damage: $damage")
    damage
  }
}

object Weapon {
  val Phaser1a = new Phaser1(2)
  val Torpedo1a = new Torpedo1(3)
}

case class Projectile(name: String, var position: Point, attack: Int, defense: Int) extends Target {
   def sprite: Sprite = {
    Oryx.sprite(s"projectile-1", 1)
  }
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

