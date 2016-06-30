package tileset

import javafx.geometry.Rectangle2D
import javafx.scene.image.{Image, ImageView}

import org.mtrupkin.math.Size
import play.api.libs.json.Json

// Created by mtrupkin on 6/17/2016.
trait Oryx {
  def imageView(sprite: String, scale: Int = 1): ImageView
}

trait SpriteSet {
  def scale: Int
  def spriteNames: Seq[String]
  def imageView(sprite: String): ImageView
}

/** Single image containing multiple sprites */
class SpriteSheet(val image: Image, val scale: Int, val spriteSheetDef: SpriteSheetDef) extends SpriteSet {
  val spriteNames: Seq[String] = spriteSheetDef.sprites.map(_.name)

  val tile = spriteSheetDef.tile * scale

  // sprites per row
  val spritePerRow = Math.round(image.getWidth / tile.width)

  def toViewport(id: Int): Rectangle2D = {
    val x = Math.floorMod(id, spritePerRow) * tile.width
    val y = Math.floorDiv(id, spritePerRow) * tile.height

    new Rectangle2D(x, y, tile.width, tile.height)
  }

  def imageView(id: Int): ImageView = {
    val imageView = new ImageView(image)
    val viewport = toViewport(id)
    imageView.setViewport(viewport)

    imageView
  }

  def imageView(sprite: SpriteDef): ImageView = imageView(sprite.tile)

  def imageView(sprite: String): ImageView = {
    val spriteDef = spriteSheetDef.sprites.find(s => s.name == sprite).get
    imageView(spriteDef)
  }
}

/** Multiple sprites each in its own image */
class SpriteSheetSliced(val scale: Int, images: Map[String, Image]) extends SpriteSet {
  val spriteNames: Seq[String] = images.keys.toSeq

  def imageView(sprite: String): ImageView = new ImageView(images(sprite))
}

object SpriteSheet {
  def loadBaseImage(filename: String): (Image, Size) = {
    val is = getClass.getResourceAsStream(filename)
    val image = new Image(is)
    (image, Size(image.getWidth.toInt, image.getHeight.toInt))
  }

  def loadImage(filename: String, size: Size, scale: Int): Image = {
    val is = getClass.getResourceAsStream(filename)
    new Image(is, size.width * scale, size.height * scale, false, false)
  }

  def scaledSpriteSheets(spriteSheetDef: SpriteSheetDef): Seq[SpriteSet] = {
    val base = loadBaseImage(spriteSheetDef.filename)

    val ss = for {
      scale <- 2 to 5
    } yield {
      val image = loadImage(spriteSheetDef.filename, base._2, scale)
      new SpriteSheet(image, scale, spriteSheetDef)
    }
    new SpriteSheet(base._1, 1, spriteSheetDef) :: ss.toList
  }

  def scaledSpriteSplicedSheets(spriteSheetSlicedDef: SpriteSheetSlicedDef): Seq[SpriteSet] = {
    val q = for {
      sprite <- spriteSheetSlicedDef.sprites
      scale <- 2 to 5
    } yield {
      new SpriteSheetSliced()
      val base = loadBaseImage(s"${spriteSheetSlicedDef.location}${sprite.tile}")
      sprite.name -> sprite.tile
    }
    q
  }
}

class OryxImpl(scaledSpriteSets: Map[Int, Seq[SpriteSet]]) extends Oryx {
  def imageView(sprite: String, scale: Int): ImageView = {
    val spriteSets = scaledSpriteSets(scale)
    val spriteSet = spriteSets.find(set => set.spriteNames.exists(_ == sprite)).get
    spriteSet.imageView(sprite)
  }
}

object Oryx extends Oryx {
  // smallest sprite is 8x8 pixels
  //
  // sprites are in multiple of smallest size
  // 8x8, 16x16, 24x24, 32x32, 40x40

  val spriteSetNames = List("oryx_16-bit_sci-fi")
  val spriteSets = spriteSetNames.flatMap(load(_))

  def load(oryxSetName: String): Seq[SpriteSet] = {
    val isOryxSetDef = getClass.getResourceAsStream(s"/$oryxSetName.json")
    val oryxSetDef = Json.parse(isOryxSetDef).as[OryxSetDef]

    val sheetSet = oryxSetDef.sheets.flatMap(sheetSetName => {
      val isDef = getClass.getResourceAsStream(s"/$oryxSetName/$sheetSetName.json")
      val spriteSheetDef = Json.parse(isDef).as[SpriteSheetDef]
      SpriteSheet.scaledSpriteSheets(spriteSheetDef)
    })

    val slicedSet = oryxSetDef.sliced.flatMap(slicedSetName => {
      val isDef = getClass.getResourceAsStream(s"/$oryxSetName/$slicedSetName.json")
      val spriteSheetDef = Json.parse(isDef).as[SpriteSlicedDef]
//      SpriteSheet.scaledSpriteSet(oryxSetName, spriteSheetDef)
      ???
    })

    sheetSet ++ slicedSet
  }

  def imageView(sprite: String, scale: Int): ImageView = {
    val spriteSet = spriteSets.find(set => set.spriteNames.exists(_ == sprite)).get
    spriteSet.imageView(sprite, scale)
  }
}

