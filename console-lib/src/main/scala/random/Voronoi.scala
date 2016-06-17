package random

import java.util.Random

/**
  * Created by mtrupkin on 5/6/2016.
  */
/// Noise module that outputs Voronoi cells.
///
/// In mathematics, a <i>Voronoi cell</i> is a region containing all the
/// points that are closer to a specific <i>seed point</i> than to any
/// other seed point.  These cells mesh with one another, producing
/// polygon-like formations.
///
/// By default, this noise module randomly places a seed point within
/// each unit cube.  By modifying the <i>frequency</i> of the seed points,
/// an application can change the distance between seed points.  The
/// higher the frequency, the closer together this noise module places
/// the seed points, which reduces the size of the cells.  To specify the
/// frequency of the cells, call the setFrequency() method.
///
/// This noise module assigns each Voronoi cell with a random constant
/// value from a coherent-noise function.  The <i>displacement value</i>
/// controls the range of random values to assign to each cell.  The
/// range of random values is +/- the displacement value.  Call the
/// setDisplacement() method to specify the displacement value.
///
/// To modify the random positions of the seed points, call the SetSeed()
/// method.
///
/// This noise module can optionally add the distance from the nearest
/// seed to the output value.  To enable this feature, call the
/// enableDistance() method.  This causes the points in the Voronoi cells
/// to increase in value the further away that point is from the nearest
/// seed point.
object Voronoi {
  private val seed: Long = 0L

  private def distance(xDist: Double, yDist: Double, zDist: Double): Double =
  {
    Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist)
  }

  def noise(x: Double, y: Double): Double = noise(x, y, 0)

  def noise(x: Double, y: Double, z: Double): Double = {
    def toInt(d: Double): Int = if (d > 0.0) d.toInt  else d.toInt - 1

    val xInt: Int = toInt(x)
    val yInt: Int = toInt(y)
    val zInt: Int = toInt(z)
    var minDist: Double = 32000000.0
    var xCandidate: Double = 0
    var yCandidate: Double = 0
    var zCandidate: Double = 0

    for {
      zCur <- (zInt - 2) to (zInt + 2)
      yCur <- (yInt - 2) to (yInt + 2)
      xCur <- (xInt - 2) to (xInt + 2)
    } {
      val xPos = xCur + valueNoise2D(xCur, yCur, zCur, seed)
      val yPos = yCur + valueNoise2D(xCur, yCur, zCur, new Random(seed).nextLong)
      val zPos = zCur + valueNoise2D(xCur, yCur, zCur, new Random(seed).nextLong)
      val xDist = xPos - x
      val yDist = yPos - y
      val zDist = zPos - z
      val dist = xDist * xDist + yDist * yDist + zDist * zDist
      if (dist < minDist) {
        minDist = dist
        xCandidate = xPos
        yCandidate = yPos
        zCandidate = zPos
      }
    }

    val xDist = xCandidate - x
    val yDist = yCandidate - y
    val zDist = zCandidate - z
    distance(xDist, yDist, zDist)
  }

  private def valueNoise2D(x: Int, y: Int, z: Int, seed: Long): Double = {
    var n: Long = (1619 * x + 31337 * y + 6971 * z + 1013 * seed) & 0x7fffffff
    n = (n >> 13) ^ n

    1.0 - (((n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff) / 1073741824.0)
  }
}
