package controller


import org.mtrupkin.math._

class CircularMotion(position: Point, heading: Vect) {
  val PI2 = Math.PI / 2.0
  def apply(cursor: Vect): Vect = {
    val d = cursor - position
    val d2 = d.normal / 2.0

    val r0 = Vect(heading.y, -heading.x)
    val theta = r0.unsignedAngle(d)

    val radius = d2 / Math.cos(theta)
    println(s"d2: $d2 radius: $radius theta: $theta")
    r0.normal(radius)
  }
}

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
