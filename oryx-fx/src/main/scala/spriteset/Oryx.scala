package spriteset

import javafx.scene.image.ImageView

import play.api.libs.json.Json

trait Oryx {
  def sprite(sprite: String, scale: Int = 1): Sprite
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

  def sprite(name: String, scale: Int): Sprite = {
    val imageViews = for {
      spriteSet <- spriteSets.filter(_.scale==scale)
      imageView <- spriteSet.sprite(name)
    } yield imageView

    imageViews.head
  }
}
