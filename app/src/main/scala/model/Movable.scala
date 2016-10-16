package model

import org.mtrupkin.math.Vect

/**
  * Created by mtrupkin on 10/15/2016.
  */
trait Movable extends Entity {
  val speed: Int
  var heading: Vect

  override def active(elapsed: Int): Unit = {
    val velocity = heading.normal(speed)
    val p1 = position + velocity
    position = p1
  }
}
