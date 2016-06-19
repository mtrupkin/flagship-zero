package sprite

import javafx.scene.image.ImageView

import org.mtrupkin.math.Point

/**
  * Created by mtrupkin on 6/18/2016.
  */
trait MapLayer {
  def apply(p: Point): Option[ImageView]
}

