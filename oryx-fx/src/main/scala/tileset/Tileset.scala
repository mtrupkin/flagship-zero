package tileset

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
case class SpriteSheetDef(name: String, tile: Int)

object SpriteDef {
  implicit val format = Json.format[SpriteDef]
}

object SpriteSheetDef {
  implicit val format = Json.format[SpriteSheetDef]
}

/**
  * Image containing sprites
  *
  * @param size default sprite dimension in tiles (8x8 pixel units)
  */
case class TilesetDef(filename: String, size: Int, width: Int, height: Int, sprites: Seq[SpriteDef])
case class TilesetSheetDef(filename: String, tileWidth: Int, tileHeight: Int, sprites: Seq[SpriteSheetDef])

object TilesetDef {
  implicit val format = Json.format[TilesetDef]
}

object TilesetSheetDef {
  implicit val format = Json.format[TilesetSheetDef]
}

/**
  * Oryx Package Set
  *
  * @param tilesets image names that contains sprites
  */
case class OryxSetDef(
  tilesets: List[String],
  sliced: List[String],
  sheets: List[String])

object OryxSetDef {
  implicit val format = Json.format[OryxSetDef]
}

case class OryxSprite(name: String, startTile: Int, size: Int)

trait SpriteSet {
  def spriteNames: Seq[String]
  def imageView(sprite: String): ImageView
}

/**
  * Single image containing multiple sprites
  */
class Tileset(image: Image, scale: Int, sprites: Seq[OryxSprite]) extends SpriteSet {
  val tileSize = 8 * scale

  // sprites per row
  protected val spriteWidth = Math.round(image.getWidth / tileSize)

  protected def toViewport(startTile: Int, size: Int): Rectangle2D = {
    val x = Math.floorMod(startTile, spriteWidth) * tileSize
    val y = Math.floorDiv(startTile, spriteWidth) * tileSize

    new Rectangle2D(x, y, tileSize*size, tileSize*size)
  }

  def scaledName(name: String): String = scale match {
    case 1 => name
    case x => s"$name-x$x"
  }

  val spriteNames: Seq[String] = sprites.map(sprite => scaledName(sprite.name))


  protected def imageView(startTile: Int, size: Int): ImageView = {
    val imageView = new ImageView(image)
    val viewport = toViewport(startTile, size)
    imageView.setViewport(viewport)

    imageView
  }

  protected def imageView(sprite: OryxSprite): ImageView = imageView(sprite.startTile, sprite.size)

  def imageView(sprite: String): ImageView = {
    val oryxSprite = sprites.find(s => scaledName(s.name) == sprite).get
    imageView(oryxSprite)
  }
}

object Tileset {
  def tileset(oryxSetName: String, tilesetDef: TilesetDef): Seq[Tileset] = {
    val filename = s"/$oryxSetName/${tilesetDef.filename}"
    val sprites = tilesetDef.sprites.map(sprite => {
      import sprite._
      OryxSprite(name, tile, size.getOrElse(tilesetDef.size))
    })

    for {
      scale <- 1 to 5
    } yield {
      val is = getClass.getResourceAsStream(filename)
      val image = new Image(is, tilesetDef.width * scale, tilesetDef.height * scale, false, false)

      new Tileset(image, scale, sprites)
    }
  }
}

case class OryxSprite(name: String, startTile: Int, size: Int)

case class SheetSprite(name: String, tile: Int,
  tileWidth: Int, tileHeight: Int,
  image: Image)

class SheetTileSet(sprites: Seq[SheetSprite]) extends SpriteSet {

}

case class SlicedSprite(name: String, image: Image)

class SlicedTileSet(sprites: Seq[SlicedSprite]) extends SpriteSet {
  val spriteNames: Seq[String] = sprites.map(_.name)

  def imageView(sprite: String): ImageView = {
    val oryxSprite = sprites.find(s => s.name == sprite).get
    new ImageView(oryxSprite.image)
  }
}

class OryxSet(
  oryxSetName: String,
  tilesets: Seq[SpriteSet]) {
  val spriteNames: Seq[String] = tilesets.flatMap(_.spriteNames)

  def imageView(sprite: String): ImageView = {
    val tileset = tilesets.find(tileset => tileset.spriteNames.exists(_ == sprite)).get
    tileset.imageView(sprite)
  }
}

object OryxSet {
  // smallest sprite is 8x8 pixels (aka tile)
  //
  // sprites are in multiple of tile sizes
  // 8x8, 16x16, 24x24, 32x32, 40x40

  def load(oryxSetName: String): OryxSet = {
    val isOryxSetDef = getClass.getResourceAsStream(s"/$oryxSetName.json")
    val oryxSetDef = Json.parse(isOryxSetDef).as[OryxSetDef]

    val tilesets = oryxSetDef.tilesets.flatMap(tilesetName => {
      val isTilesetDef = getClass.getResourceAsStream(s"/$oryxSetName/$tilesetName.json")
      val tilesetDef = Json.parse(isTilesetDef).as[TilesetDef]
      Tileset.tileset(oryxSetName, tilesetDef)
    })

    new OryxSet(oryxSetName, tilesets)
  }
}

object Oryx {
  protected val oryxSetNames = List(
    "oryx_16-bit_sci-fi"
    ,"oryx_lofi_horror"
  )

  protected val oryxSets: Seq[OryxSet] = oryxSetNames.map(OryxSet.load(_))

  def imageView(sprite: String): ImageView = {
    val oryxSet = oryxSets.find(oryxSet => oryxSet.spriteNames.exists(_ == sprite)).get
    oryxSet.imageView(sprite)
  }
}