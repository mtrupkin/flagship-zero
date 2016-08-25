package spriteset

import javafx.geometry.Rectangle2D
import javafx.scene.image.{Image, ImageView}

import org.mtrupkin.math.Size

/** size in pixels units */
/** smallest sprite size is 8x8 pixels */

case class Sprite(name: String, imageView: ImageView, size: Size)

// Created by mtrupkin on 6/17/2016.
trait SpriteSet {
  def scale: Int
  def sprite(sprite: String): Option[Sprite]
}

/** Single image containing multiple sprites */
class SpriteSheet(val image: Image, val spriteSheetDef: SpriteSheetDef, val scale: Int) extends SpriteSet {
  val spritePixelSize = spriteSheetDef.size * scale * SPRITE_UNIT_PIXEL

  // sprites per row
  val spritePerRow = Math.round(image.getWidth / spritePixelSize)

  def viewport(id: Int): Rectangle2D = {
    val x = Math.floorMod(id, spritePerRow) * spritePixelSize
    val y = Math.floorDiv(id, spritePerRow) * spritePixelSize

    new Rectangle2D(x, y, spritePixelSize, spritePixelSize)
  }

  def sprite(name: String, id: Int): Sprite = {
    val imageView = new ImageView(image)
    imageView.setViewport(viewport(id))

    Sprite(name, imageView, Size(spritePixelSize, spritePixelSize))
  }

  def sprite(name: String): Option[Sprite] = {
    spriteSheetDef.sprites.find(_.name == name).map(s => sprite(name, s.tile))
  }
}

/** Multiple sprites each in its own image */
class SpriteSheetSliced(val scale: Int, images: Map[String, Image]) extends SpriteSet {
  def sprite(sprite: String): Option[Sprite] = images.get(sprite).map {
    image => Sprite(sprite, new ImageView(image), Size(image.getWidth.toInt, image.getHeight.toInt))
  }
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
      new SpriteSheet(scaleImage, spriteSheetDef, scale)
    }
    new SpriteSheet(image, spriteSheetDef, 1) :: ss.toList
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



