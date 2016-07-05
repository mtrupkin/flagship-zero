import org.mtrupkin.control.CoordinateConverter
import org.mtrupkin.math.{Point, Size}
import org.scalatest.{FunSuite, Matchers}


class CoordinateTest extends FunSuite with Matchers {
  val screenSize = Size(640, 640)
  val quadrantSize = Size(100, 100)
  val converter = new CoordinateConverter(screenSize, quadrantSize)

  test("world to screen: center") {
    val expectedCenter: Point = screenSize / 2
    val actualCenter = converter.toScreen(Point.Origin)
    expectedCenter should equal(actualCenter)
  }

  test("world to screen: upper left") {
    val expectedUpperLeft: Point = Point.Origin
    val actualUpperLeft = converter.toScreen(-quadrantSize)
    expectedUpperLeft should equal(actualUpperLeft)
  }

  test("world to screen: lower right") {
    val expectedLowerRight: Point = screenSize
    val actualLowerRight = converter.toScreen(quadrantSize)
    expectedLowerRight should equal (actualLowerRight)
  }
}
