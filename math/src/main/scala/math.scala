package org.mtrupkin

package object math {
  import Math._

  def limit(x: Double, min: Double, max: Double): Double =
    if (x < min) min else if (x > max) max else x

  def arcCosine(x: Double): Double = acos(limit(x, -1, 1))
}
