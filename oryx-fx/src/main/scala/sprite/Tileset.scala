package sprite

import javafx.geometry.Rectangle2D
import javafx.scene.image.{Image, ImageView}

import play.api.libs.json.Json

// Created by mtrupkin on 6/17/2016.

/**
  * Individual Sprite
  *
  * @param name
  * @param tile location of upper left corner of sprite by tile id
  * @param size dimension of sprite in tile units
  */
case class SpriteDef(name: String, tile: Int, size: Option[Int])

/**
  * Image containing sprites
  *
  * @param size default sprite dimension in tiles (8x8 pixel units)
  */
case class TilesetDef(filename: String, size: Int, sprites: List[SpriteDef])

/**
  * Oryx Package Set
  *
  * @param tilesets image names that contains sprites
  */
case class OryxSetDef(tilesets: List[String])

case class Sprite(name: String, startTile: Int, size: Int)

/**
  * Single image containing multiple sprites
  * @param image
  * @param sprites
  */
class Tileset(image: Image, sprites: List[Sprite]) {
  import OryxSet._

  // sprites per row
  protected val spriteWidth = Math.round(image.getWidth / tileWidth)

  protected def viewport(startTile: Int, size: Int): Rectangle2D = {
    val x = Math.floorMod(startTile, spriteWidth)
    val y = Math.floorDiv(startTile, spriteWidth)

    new Rectangle2D(x, y, tileWidth*size, tileHeight*size)
  }

  protected def imageView(startTile: Int, size: Int): ImageView = {
    val imageView = new ImageView(image)
    val viewport = viewport(startTile, size)
    imageView.setViewport(viewport)
    imageView
  }

  val views: Map[String, ImageView] = sprites.map(sprite => {
    sprite.name -> imageView(sprite.startTile, sprite.size)
  }).toMap
}

object Tileset {
  def tileset(oryxSetName: String, tilesetDef: TilesetDef): Tileset = {
    val isImage = getClass.getResourceAsStream(s"/$oryxSetName/${tilesetDef.filename}.json")
    val sprites = tilesetDef.sprites.map(sprite => {
      import sprite._
      Sprite(name, tile, size.getOrElse(tilesetDef.size))
    })

    new Tileset(new Image(isImage), sprites)
  }
}

class OryxSet(oryxSetName: String, tilesets: List[Tileset]) {
  val views = tilesets.map(tileset => tileset.views).reduce(_++_)

  def view(spriteName: String): ImageView = views(spriteName)
}

object OryxSet {
  // smallest sprite is 8x8 pixels (aka tile)
  //
  // sprites are in multiple of tile sizes
  // 8x8, 16x16, 24x24, 32x32, 40x40
  val tileWidth = 8
  val tileHeight = 8

  def load(oryxSetName: String): OryxSet = {
    val isOryxSetDef = getClass.getResourceAsStream(s"/$oryxSetName.json")
    val oryxSetDef = Json.parse(isOryxSetDef).as[OryxSetDef]

    val tilesets = oryxSetDef.tilesets.map(tilesetName => {
      val isTilesetDef = getClass.getResourceAsStream(s"/$oryxSetName/$tilesetName.json")
      val tilesetDef = Json.parse(isTilesetDef).as[TilesetDef]
      Tileset.tileset(oryxSetName, tilesetDef)
    })

    new OryxSet(oryxSetName, tilesets)
  }
}

object Oryx {
  protected val sets = List("oryx_16-bit_sci-fi", "oryx_lofi_horror")
  protected val views: Map[String, ImageView] = sets.map(OryxSet.load(_).views).reduce(_++_)

  def view(name: String): ImageView = views(name)
}