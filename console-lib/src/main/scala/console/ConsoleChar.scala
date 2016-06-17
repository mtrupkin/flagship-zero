package org.mtrupkin.console

import javafx.scene.image.Image

import scala.collection.immutable.HashMap


object TileSets {
  val tileSets = new HashMap[String, Image]

  def addTileSet(name: String, image: Image): Unit = {

  }
  def tileSet(name: String): Image = tileSets.get(name).get
}

class Sprite (sprite: String, id: Int, tileSet: String)

