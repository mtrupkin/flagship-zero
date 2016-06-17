package org.mtrupkin.random

/**
  * Created by martin on 20/10/15.
  */
object Perlin {
  val permutation = List(151,160,137,91,90,15,					// Hash lookup table as defined by Ken Perlin.  This is a randomly
    131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,	// arranged array of all numbers from 0-255 inclusive.
    190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
    88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
    77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
    102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
    135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
    5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
    223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
    129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
    251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
    49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
    138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180)

  // Initialize the p array with two copies of the permutation array.
  val p = permutation ++ permutation

  /**
    *  Three dimensional Perlin noise.
    *  Based on Ken Perlins reference implementation of improved noise ( http://mrl.nyu.edu/~perlin/noise/ ).
    *
    * @param x coordinate to calculate the noise value at
    * @param y coordinate to calculate the noise value at
    * @param z coordinate to calculate the noise value at
    * @return the noise value at the specified point, a value between -1 and 1
    */
  def noise(x: Double, y: Double = 0, z: Double = 0): Double =
  {

    def fade(t: Double): Double =
    {
      // fade(t) = 6t^5 - 15t^4 + 10t^3
      t * t * t * (t * (t * 6 - 15) + 10)
    }

    def lerp(t: Double, a: Double, b: Double) : Double =
    {
      a + t * (b - a)
    }

    def grad(hash: Int, x: Double, y: Double, z: Double): Double =
    {
      (hash & 0xF) match {
        case 0x0 =>  x + y
        case 0x1 => -x + y
        case 0x2 =>  x - y
        case 0x3 => -x - y
        case 0x4 =>  x + z
        case 0x5 => -x + z
        case 0x6 =>  x - z
        case 0x7 => -x - z
        case 0x8 =>  y + z
        case 0x9 => -y + z
        case 0xA =>  y - z
        case 0xB => -y - z
        case 0xC =>  y + x
        case 0xD => -y + z
        case 0xE =>  y - x
        case 0xF => -y - z
        case _ => ???
      }
    }

    // Calculate integer values for coordinates
    val roundedX = x.toInt
    val roundedY = y.toInt
    val roundedZ = z.toInt

    // Find unit cube that contains point
    val X: Int = roundedX & 255
    val Y: Int = roundedY & 255
    val Z: Int = roundedZ & 255

    // Find relative x,y,z of point in cube
    val x_ = x - roundedX
    val y_ = y - roundedY
    val z_ = z - roundedZ

    // Compute fade curves for each of x,y,z
    val u = fade(x_)
    val v = fade(y_)
    val w = fade(z_)

    // Hash coordinates of the 8 cube corners,
    val A = p(X) + Y
    val AA = p(A) + Z
    val AB = p(A + 1) + Z
    val B = p(X + 1) + Y
    val BA = p(B) + Z
    val BB = p(B + 1) + Z

    // and add blended results from 8 corners of cube
    val noise = lerp(w,
      lerp(v,
        lerp(u,
          grad(p(AA), x_, y_, z_),
          grad(p(BA), x_ - 1, y_, z_)),
        lerp(u,
          grad(p(AB), x_, y_ - 1, z_),
          grad(p(BB), x_ - 1, y_ - 1, z_))),
      lerp(v,
        lerp(u,
          grad(p(AA + 1), x_, y_, z_ - 1),
          grad(p(BA + 1), x_ - 1, y_, z_ - 1)),
        lerp(u,
          grad(p(AB + 1), x_ , y_ - 1, z_ - 1),
          grad(p(BB + 1), x_ - 1, y_ - 1, z_ - 1))))
    noise
  }
}
