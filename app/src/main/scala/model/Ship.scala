package model

import org.mtrupkin.math._
import spriteset.{Oryx, Sprite}

/**
  * Created by mtrupkin on 7/21/2016.
  */
case class Ship(
    name: String,
    subtype: String,
    faction: String,
    position: Point,
    heading: Vect = Vect.Up,
    maximumSpeed: Int = 10) extends Target {

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

