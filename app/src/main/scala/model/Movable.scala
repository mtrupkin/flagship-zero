package model

import org.mtrupkin.math.Vect

/**
  * Created by mtrupkin on 10/15/2016.
  */
trait Movable extends Entity {
  var speed: Int
  var heading: Vect

  // elapsed in milliseconds
  override def activated(elapsed: Int): Unit = {
    val velocity = heading.normal(speed)
    val dx = velocity * (elapsed / 1000.0)
    val p1 = position + dx
    position = p1
  }
}
