import org.mtrupkin.control.CoordinateConverter
import org.mtrupkin.math.{Point, Size}
import org.scalatest.{FunSuite, Matchers}


class CoordinateTest extends FunSuite with Matchers {
  val screen = Size(640, 640)
  val quadrant = Size(100, 100)
  val converter = CoordinateConverter(screen, quadrant)

  test("world to screen: center") {
    val expected: Point = screen / 2
    val actual = converter.toScreen(Point.Origin)
    actual should equal(expected)
  }

  test("world to screen: upper left") {
    val expected: Point = Point.Origin
    val upperLeft = Point(-quadrant.width, quadrant.height)
    val actual = converter.toScreen(upperLeft)
    actual should equal(expected)
  }

  test("world to screen: upper right") {
    val expected: Point = Point(screen.width, 0)
    val upperRight = Point(quadrant.width, quadrant.height)
    val actual = converter.toScreen(upperRight)
    actual should equal(expected)
  }

  test("world to screen: lower right") {
    val expected: Point = screen
    val lowerRight = Point(quadrant.width, -quadrant.height)
    val actual = converter.toScreen(lowerRight)
    actual should equal(expected)
  }

  test("world to screen: lower left") {
    val expected: Point = Point(0, screen.height)
    val lowerLeft = -quadrant
    val actual = converter.toScreen(lowerLeft)
    actual should equal(expected)
  }

  test("screen to world: center") {
    val expected: Point = Point.Origin
    val actual = converter.toWorld(screen / 2)
    actual should equal(expected)
  }

  test("screen to world: upper left") {
    val expected: Point =  Point(-quadrant.width, quadrant.height)
    val upperLeft = Point.Origin
    val actual = converter.toWorld(upperLeft)
    actual should equal(expected)
  }

  test("screen to world: upper right") {
    val expected: Point = Point(quadrant.width, quadrant.height)
    val upperRight = Point(screen.width, 0)
    val actual = converter.toWorld(upperRight)
    actual should equal(expected)
  }

  test("screen to world: lower right") {
    val expected: Point = Point(quadrant.width, -quadrant.height)
    val lowerRight = screen
    val actual = converter.toWorld(lowerRight)
    actual should equal(expected)
  }

  test("screen to world: lower left") {
    val expected: Point = -quadrant
    val lowerLeft = Point(0, screen.height)
    val actual = converter.toWorld(lowerLeft)
    actual should equal(expected)
  }
}
