package tileset

import javafx.geometry.Rectangle2D
import javafx.scene.image.{Image, ImageView}

import org.mtrupkin.math.Size
import play.api.libs.json.Json

// Created by mtrupkin on 6/17/2016.
trait SpriteSet {
  def scale: Int
  def imageView(sprite: String): Option[ImageView]
}

/** Single image containing multiple sprites */
class SpriteSheet(val image: Image, val scale: Int, val spriteSheetDef: SpriteSheetDef) extends SpriteSet {
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

  def imageView(sprite: String): Option[ImageView] = {
    spriteSheetDef.sprites.find(s => s.name == sprite).map(imageView(_))
  }
}

/** Multiple sprites each in its own image */
class SpriteSheetSliced(val scale: Int, images: Map[String, Image]) extends SpriteSet {
  def imageView(sprite: String): Option[ImageView] = images.get(sprite).map(new ImageView(_))
}

class SpriteSheetLoader(location: String) {
  def loadImage(filename: String): (Image, Size) = {
    val filepath = s"/$location/$filename"
    val is = getClass.getResourceAsStream(filepath)
    val image = new Image(is)
    (image, Size(image.getWidth.toInt, image.getHeight.toInt))
  }

  def loadScaleImage(filename: String, size: Size, scale: Int): Image = {
    val is = getClass.getResourceAsStream(s"/$location/$filename")
    new Image(is, size.width * scale, size.height * scale, false, false)
  }

  def scaledSpriteSheets(spriteSheetDef: SpriteSheetDef): Seq[SpriteSet] = {
    val (image, size) = loadImage(spriteSheetDef.filename)

    val ss = for {
      scale <- 2 to 5
    } yield {
      val scaleImage = loadScaleImage(spriteSheetDef.filename, size, scale)
      new SpriteSheet(scaleImage, scale, spriteSheetDef)
    }
    new SpriteSheet(image, 1, spriteSheetDef) :: ss.toList
  }

  def scaledSpriteSlicedSheets(spriteSheetSlicedDef: SpriteSheetSlicedDef): Seq[SpriteSet] = {
    def filepath(tile: String): String = s"${spriteSheetSlicedDef.location}$tile.png"

    val base = for {
      sprite <- spriteSheetSlicedDef.sprites
    } yield {
      val (image, size) = loadImage(filepath(sprite.tile))
      (sprite, image, size)
    }

    val imageMap1 = base.map(s=>(s._1.name->s._2)).toMap
    val sheet1 = new SpriteSheetSliced(1, imageMap1)

    val imageMap2 = base.map( s => {
      val (sprite, image, size) = s
      val scaledImage = loadScaleImage(filepath(sprite.tile), size, 2)
      sprite.name -> scaledImage
    }).toMap

    val sheet2 = new SpriteSheetSliced(2, imageMap2)

    List(sheet1, sheet2)
  }
}

trait Oryx {
  def imageView(sprite: String, scale: Int = 1): ImageView
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

    val spriteSheetLoader = new SpriteSheetLoader(oryxSetName)

    val sheetSet = oryxSetDef.sheets.flatMap(sheetSetName => {
      val isDef = getClass.getResourceAsStream(s"/$oryxSetName/$sheetSetName.json")
      val spriteSheetDef = Json.parse(isDef).as[SpriteSheetDef]
      spriteSheetLoader.scaledSpriteSheets(spriteSheetDef)
    })

    val slicedSet = oryxSetDef.sliced.flatMap(slicedSetName => {
      val isDef = getClass.getResourceAsStream(s"/$oryxSetName/$slicedSetName.json")
      val spriteSheetSlicedDef = Json.parse(isDef).as[SpriteSheetSlicedDef]
      spriteSheetLoader.scaledSpriteSlicedSheets(spriteSheetSlicedDef)
    })

    sheetSet ++ slicedSet
  }

  def imageView(sprite: String, scale: Int): ImageView = {
    val imageViews = for {
      spriteSet <- spriteSets.filter(_.scale==scale)
      imageView <- spriteSet.imageView(sprite)
    } yield imageView

    imageViews.head
  }
}

