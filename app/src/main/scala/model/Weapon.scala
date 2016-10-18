package model

import org.mtrupkin.math.{Point, Vect}
import spriteset.{Oryx, Sprite}

import scalafx.beans.property.{ObjectProperty, StringProperty}

/**
  *
  */
trait Weapon {
  def name: String
  def rating: Int
  def attack(range: Double): Int

  val nameProp = new StringProperty(this, "name", name)
  val ratingProp = new ObjectProperty(this, "rating", rating)
}

trait DirectWeapon extends Weapon {
  def fire(target: Entity): Unit = {
  }
}

trait TorpedoWeapon extends Weapon {
  def fire(position: Point, target: Entity): Projectile = {
    val heading = target.position - position
    val projectile = Projectile(name, position, heading, 1, 1)
    projectile.active = true
    projectile
  }
}

case class Phaser1(rating: Int) extends DirectWeapon {
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

case class Torpedo1(rating: Int) extends TorpedoWeapon {
  lazy val name = "Torpedo-1"
  def attack(range: Double): Int = {
    val damage = Combat.attack(rating, 2)
    println(s"damage: $damage")
    damage
  }
}

case class Projectile(
   name: String,
   var position: Point,
   var heading: Vect,
   attack: Int,
   defense: Int) extends Movable with Targetable {
  var speed: Int = 20

  def sprite: Sprite = {
    Oryx.sprite(s"projectile-1", 1)
  }
}

object Weapon {
  val Phaser1a = new Phaser1(2)
  val Torpedo1a = new Torpedo1(3)
}