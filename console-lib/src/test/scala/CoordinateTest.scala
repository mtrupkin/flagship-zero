import console.Transform
import org.mtrupkin.math.{Point, Size}
import org.scalatest.{FunSuite, Matchers}


class CoordinateTest extends FunSuite with Matchers {
  val screen = Size(640, 640)
  val world = Size(200, 200)
  val quadrant = world / 2
  val converter = Transform(screen, world)

  test("world to screen: center") {
    val expected: Point = screen / 2
    val actual = converter.screen(Point.Origin)
    actual should equal(expected)
  }

  test("world round trip: center") {
    val expected: Point = Point.Origin
    val screen = converter.screen(expected)
    val actual = converter.world(screen)

    actual should equal(expected)
  }

  test("world to screen: lower right") {
    val expected: Point = screen
    val lowerRight = Point(quadrant.width, -quadrant.height)
    val actual = converter.screen(lowerRight)
    actual should equal(expected)
  }

  test("world round trip: lower right") {
    val expected: Point = Point(quadrant.width, -quadrant.height)
    val screen = converter.screen(expected)
    val actual = converter.world(screen)

    actual should equal(expected)
  }



  test("world to screen: upper left") {
    val expected: Point = Point.Origin
    val upperLeft = Point(-quadrant.width, quadrant.height)
    val actual = converter.screen(upperLeft)
    actual should equal(expected)
  }

  test("world to screen: upper right") {
    val expected: Point = Point(screen.width, 0)
    val upperRight = Point(quadrant.width, quadrant.height)
    val actual = converter.screen(upperRight)
    actual should equal(expected)
  }

  test("world to screen: lower left") {
    val expected: Point = Point(0, screen.height)
    val lowerLeft =  Point(-quadrant.width, -quadrant.height)
    val actual = converter.screen(lowerLeft)
    actual should equal(expected)
  }

  test("screen to world: center") {
    val expected: Point = Point.Origin
    val center: Point = Size.toPoint(screen / 2)
    val actual = converter.world(center)
    actual should equal(expected)
  }

  test("screen to world: upper left") {
    val expected: Point =  Point(-quadrant.width, quadrant.height)
    val upperLeft = Point.Origin
    val actual = converter.world(upperLeft)
    actual should equal(expected)
  }

  test("screen to world: upper right") {
    val expected: Point = Point(quadrant.width, quadrant.height)
    val upperRight = Point(screen.width, 0)
    val actual = converter.world(upperRight)
    actual should equal(expected)
  }

  test("screen to world: lower right") {
    val expected: Point = Point(quadrant.width, -quadrant.height)
    val lowerRight: Point = screen
    val actual = converter.world(lowerRight)
    actual should equal(expected)
  }

  test("screen to world: lower left") {
    val expected: Point = Point(-quadrant.width, -quadrant.height)
    val lowerLeft = Point(0, screen.height)
    val actual = converter.world(lowerLeft)
    actual should equal(expected)
  }
}


class OffsetCoordinateTest extends FunSuite with Matchers {
  val screen = Size(640, 640)
  val world = Size(200, 200)
  val quadrant = world / 2
  val converter = Transform(screen, world)
  val origin = Point(50, 50)
  converter.origin = origin

  test("world to screen: center") {
    val expected: Point = screen / 2
    val actual = converter.screen(origin)
    actual should equal(expected)
  }

  test("world round trip: center") {
    val expected: Point = origin
    val screen = converter.screen(origin)
    val actual = converter.world(screen)
    actual should equal(expected)
  }

  test("world to screen: upper left") {
    val expected: Point = Point.Origin
    val upperLeft = Point(-quadrant.width, quadrant.height) + origin
    val actual = converter.screen(upperLeft)
    actual should equal(expected)
  }

  test("world round trip: upper left") {
    val expected = Point(-quadrant.width, quadrant.height) + origin
    val screen = converter.screen(expected)
    val actual = converter.world(screen)
    actual should equal(expected)
  }

  test("world to screen: upper right") {
    val expected: Point = Point(screen.width, 0)
    val upperRight = Point(quadrant.width, quadrant.height) + origin
    val actual = converter.screen(upperRight)
    actual should equal(expected)
  }

  test("world to screen: lower right") {
    val expected: Point = screen
    val lowerRight = Point(quadrant.width, -quadrant.height) + origin
    val actual = converter.screen(lowerRight)
    actual should equal(expected)
  }

  test("world to screen: lower left") {
    val expected: Point = Point(0, screen.height)
    val lowerLeft = Point(-quadrant.width, -quadrant.height) + origin
    val actual = converter.screen(lowerLeft)
    actual should equal(expected)
  }

  test("screen to world: center") {
    val expected: Point = origin
    val center: Point = screen / 2
    val actual = converter.world(center)
    actual should equal(expected)
  }

  test("screen to world: upper left") {
    val expected: Point =  Point(-quadrant.width, quadrant.height) + origin
    val upperLeft = Point.Origin
    val actual = converter.world(upperLeft)
    actual should equal(expected)
  }

  test("screen to world: upper right") {
    val expected: Point = Point(quadrant.width, quadrant.height) + origin
    val upperRight = Point(screen.width, 0)
    val actual = converter.world(upperRight)
    actual should equal(expected)
  }

  test("screen to world: lower right") {
    val expected: Point = Point(quadrant.width, -quadrant.height) + origin
    val lowerRight: Point = screen
    val actual = converter.world(lowerRight)
    actual should equal(expected)
  }

  test("screen to world: lower left") {
    val expected: Point = Point(-quadrant.width, -quadrant.height) + origin
    val lowerLeft = Point(0, screen.height)
    val actual = converter.world(lowerLeft)
    actual should equal(expected)
  }
}
