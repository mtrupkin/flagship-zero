package random

/**
  * Created by mtrupkin on 5/9/2016.
  */

object NoiseBuilder {

  type Noise = (Double, Double) => Double

  def limit(x: Double, min: Double, max: Double): Double =
    if (x < min) min else if (x > max) max else x

  // maps a function that ranges over (in_min, in_max) to range over (out_min, out_max)
  def map(value: Double, in_min: Double, in_max: Double, out_min: Double, out_max: Double): Double = {
    val scaled = ((value-in_min)/(in_max-in_min))*(out_max-out_min) + out_min
    limit(scaled, out_min, out_max)
  }

  // maps a function that ranges over min to max to (0, 1)
  def map(value: Double, min: Double, max: Double): Double = map(value, min, max, 0.0, 1.0)

  def modulate(frequency: Double, f: Noise): Noise =
    (x: Double, y: Double) => f(frequency * x , frequency * y)

  def modulate(frequency: Double, amplitude: Double, f: Noise): Noise = {
    (x: Double, y: Double) => modulate(frequency, f)(x, y) * amplitude
  }

  def octaves(persistence: Double, f: Noise, intervals: Int = 4): Noise = {
    (x: Double, y: Double) => {
      var total = 0.0
      var frequency = 1.0
      var amplitude = 1.0
      var maxValue = 0.0

      for {
        i <- 0 to intervals
      } {
        total += modulate(frequency, amplitude, f)(x, y)
        maxValue += amplitude

        amplitude *= persistence
        frequency *= 2
      }

      total / maxValue
    }
  }
}
