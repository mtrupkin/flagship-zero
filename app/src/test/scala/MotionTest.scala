import controller.CircularMotion
import org.mtrupkin.math.Point
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by mtrupkin on 9/7/2016.
  */
class MotionTest extends FunSuite with Matchers {
  val p = Point(0, 0)
  val heading = Point(1, 0)

  val motion = new CircularMotion(p, heading)

  test("motion radius to (10, 100)") {
    val p1 = Point(10,100)
    val expected: Double = 50.295
    val actual: Double = motion(p1).normal

    actual should equal(expected)
  }
}
