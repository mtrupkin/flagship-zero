package spriteset

import org.mtrupkin.math.Size
import play.api.libs.json.Json

// Created by mtrupkin on 6/29/2016.

/** Individual sprite in a sheet */
case class SpriteDef(name: String, tile: Int)

/** Individual spliced sprite */
case class SpriteSlicedDef(name: String, tile: String)

/** Single image containing multiple sprites */
case class SpriteSheetDef(filename: String, size: Int, sprites: Seq[SpriteDef])

/** Multiple images, each containing a single sprite */
case class SpriteSheetSlicedDef(location: String, sprites: Seq[SpriteSlicedDef])

/** Oryx Package Set */
case class OryxSetDef(sheets: List[String], sliced: List[String])

object SpriteDef {
  implicit val format = Json.format[SpriteDef]
}

object SpriteSlicedDef {
  implicit val format = Json.format[SpriteSlicedDef]
}

object SpriteSheetDef {
  implicit val sizeFormat = Json.format[Size]
  implicit val format = Json.format[SpriteSheetDef]
}

object SpriteSheetSlicedDef {
  implicit val format = Json.format[SpriteSheetSlicedDef]
}

object OryxSetDef {
  implicit val format = Json.format[OryxSetDef]
}
