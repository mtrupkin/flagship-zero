package model

import javafx.scene.image.ImageView

import org.mtrupkin.cell.Cell
import org.mtrupkin.math.{Matrix, Point, Size}
import sprite.{MapLayer, Oryx}

/**
  * Created by mtrupkin on 3/22/2016.
  */
trait GameObject {
  def name: String
  def position: Point
  def spriteName: String
}

case class Planet(name: String, position: Point) extends GameObject {
  def spriteName: String = "planet-blue"
}

class World {
  val planet1 = Planet("Earth", Point(5,5))
  val objects: List[GameObject] = List(planet1)
  def mapLayer: MapLayer = new MapLayer {
    def apply(p: Point): Option[ImageView] = {
      for {
        o <- objects.find(_.position == p)

      } yield {
        Oryx.view(o.spriteName)
      }
    }

  }
}

object WorldBuilder {
  def apply(): World = {
    val world = new World
    world
  }
}
