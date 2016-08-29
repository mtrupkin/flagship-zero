package model

import org.mtrupkin.math._
import spriteset.{Oryx, Sprite}

case class Weapon(name: String, rating: Int)

object Weapon {
  val Phaser1 = new Weapon("Phaser-1", 2)
}

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    faction: String,
    position: Point,
    heading: Vect = Vect.Up,
    maximumSpeed: Int = 25,
    maxShields: Int = 5,
    weapons: Seq[Weapon] = Nil) extends Target {

  var shields = maxShields

  def damage(amount: Int): Unit = {
    shields -= amount
    if (shields < 0) { println("Destroyed!!!") }
  }

  override def sprite: Sprite = {
    val sprite = Oryx.sprite(s"${Ship.subtypeMap(subtype)}-${Ship.factionMap(faction)}", 2)

    sprite.imageView.setRotate(-heading.unsignedAngle*180/Math.PI)
    sprite
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

