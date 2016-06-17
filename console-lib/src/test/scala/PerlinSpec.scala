/**
  * Created by mtrupkin on 3/25/2016.
  */

import org.mtrupkin.random.Perlin

import collection.mutable.Stack
import org.scalatest._

class PerlinSpec extends FlatSpec with Matchers {

  "Perlin noise" should "generate a value" in {
    val (x, y) = (0.1, 0.5)
    val noise = Perlin.noise(x, y)

    val beInRange = be > -1.0 and be < 1.0
    noise should beInRange
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}
