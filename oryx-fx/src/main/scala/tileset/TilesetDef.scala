package tileset

import org.mtrupkin.math.Size
import play.api.libs.json.Json

// Created by mtrupkin on 6/29/2016.

/** Individual sprite in a sheet */
case class SpriteDef(name: String, tile: Int)

/** Individual spliced sprite */
case class SpriteSlicedDef(name: String, tile: String)

/** Image containing sprites */
case class SpriteSheetDef(filename: String, tile: Size, sprites: Seq[SpriteDef])

/** Multiple images containing a single sprite */
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
  implicit val format = Json.format[SpriteSheetDef]
}

object SpriteSheetSlicedDef {
  implicit val format = Json.format[SpriteSheetSlicedDef]
}

object OryxSetDef {
  implicit val format = Json.format[OryxSetDef]
}
