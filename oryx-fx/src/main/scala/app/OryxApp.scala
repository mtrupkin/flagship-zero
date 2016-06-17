package app

import play.api.libs.json.Json
import sprite.OryxSet


/**
  * Created by mtrupkin on 6/17/2016.
  */
object OryxApp extends App {
  val is = getClass.getResourceAsStream("/oryx_16-bit_sci-fi.json")

  val oryxSet = Json.parse(is).as[OryxSet]

  oryxSet.tilesets.foreach(tileset => {
  })
}
