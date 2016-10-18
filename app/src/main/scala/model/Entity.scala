package model

import org.mtrupkin.math.Point
import spriteset.{Oryx, Sprite}

/** Created by mtrupkin on 6/30/2016. */
trait Entity {
  var position: Point
  def sprite: Sprite

  def update(elapsed: Int): Unit = {}

  var active: Boolean = false
  def activated(elapsed: Int): Unit = {}
  def startTurn(): Unit = { active = true }
  def endTurn(): Unit = { active = false }
}

case class StaticEntity(var position: Point, sprite: Sprite) extends Entity

trait Targetable extends Entity {
  def name: String
}

trait Subtyped extends Targetable {
  def subtype: String
  val subtypeMap: Map[String, String]
  def sprite(): Sprite = Oryx.sprite(subtypeMap(subtype))
}

case class Star(name: String, subtype: String, var position: Point) extends Subtyped {
  // star classes
  // O hot
  // B hot super-giant
  // A dwarf star
  // F
  // G sol-like
  // K cool giant
  // M cool super-giant / dwarf
  implicit val subtypeMap: Map[String, String] = Map(
    "O" -> "star",
    "B" -> "star",
    "A" -> "star",
    "F" -> "star",
    "G" -> "star",
    "K" -> "star",
    "M" -> "star"
  )
}

case class Planet(name: String, subtype: String, var position: Point) extends Subtyped {
  // M earth-like
  // A aquatic
  // B barren
  // D desert
  // F frozen
  // G gas giant
  // P poisonous
  implicit val subtypeMap = Map(
    "M" -> "planet-blue",
    "A" -> "planet-blue-rings",
    "B" -> "planet-gray",
    "D" -> "planet-rock",
    "F" -> "planet-gray-exploded",
    "G" -> "planet-blue-large",
    "P" -> "planet-purple"
  )
}
