package controller


import org.mtrupkin.math._

class TieredMotion(position: Point, heading: Vect) {

  def apply(cursor: Vect): Vect = {
    val r = cursor - position
    val distance = r.normal
    val theta = heading.angle(r)
    val maxDistance = Math.abs(theta) match {
      case t if (t < Math.PI/16) => 50
      case t if (t < Math.PI/4) => 25
//      case t if (t < Math.PI/4) => 15
      case _ => 10
    }

    r.normal(Math.min(distance, maxDistance))
  }
}
