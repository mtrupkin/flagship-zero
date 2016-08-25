package controller

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.Pane

import console.{Transform}
import control.GameConsole
import model._
import org.mtrupkin.control.SpriteConsole
import org.mtrupkin.math.{Point, Size, Vect}

import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyCode._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import scala.util.Random


trait Game { self: Controller =>
  class GameController(val world: World) extends ControllerState {
    val name = "Game"

    var shipMovement = new ShipMovement(world.ship)
    var shipTarget: Option[Target] = None
    var cursorTarget: Option[Target] = None

    def target: Option[Target] = shipTarget.orElse(cursorTarget)

    @FXML var consolePane: Pane = _
    @FXML var targetLabel: Label = _
    @FXML var targetTypeLabel: Label = _
    @FXML var targetPositionLabel: Label = _
    @FXML var targetDistanceLabel: Label = _
    @FXML var mouseWorldLabel: Label = _
    @FXML var mouseScreenLabel: Label = _

    val screenSize = Size(720, 720)
    //  // world unit size is one sprite unit
    //  // world size (90, 90)
    val worldSize = screenSize / spriteset.SPRITE_UNIT_PIXEL
    val transform = Transform(screenSize, worldSize)

    val console = GameConsole(transform)

    def initialize(): Unit = {
      new sfxl.Pane(consolePane) {
        filterEvent(sfxi.KeyEvent.KeyPressed) {
          (event: sfxi.KeyEvent) => handleKeyPressed(event)
        }
      }

      {
        import console._

        onMouseClicked = (e: sfxi.MouseEvent) => handleMouseClicked(e)
        onMousePressed = (e: sfxi.MouseEvent) => handleMousePressed(e)
        onMouseMoved = (e: sfxi.MouseEvent) => handleMouseMove(e)
        onMouseExited = (e: sfxi.MouseEvent) => handleMouseExit(e)
        onMouseDragged = (e: sfxi.MouseEvent) => handleMouseDragged(e)
        onMouseDragExited = (e: sfxi.MouseEvent) => handleMouseDragExited(e)
        onMouseReleased = (e: sfxi.MouseEvent) => handleMouseReleased(e)
        onKeyPressed = (e: sfxi.KeyEvent) => handleKeyPressed(e)
      }


      consolePane.getChildren.clear()
      consolePane.getChildren.add(console)

      consolePane.setFocusTraversable(true)
    }

    override def update(elapsed: Int): Unit = {
      import world._
      implicit val origin = ship.position


      console.clear()
      entities.foreach(console.draw(_))
      console.draw(ship.copy(heading = shipMovement.heading))
      // console.drawVect(ship.position, shipMovement.heading)
      cursorTarget.foreach(console.cursor(_))
      shipTarget.foreach(console.target(_))
    }

    def handleMouseDragged(event: sfxi.MouseEvent): Unit = {
      if (event.isSecondaryButtonDown) {
        val cursor = world(event)
        if (event.shiftDown) {
          shipMovement.rotate(cursor)
        } else {
          shipMovement.move(cursor)
        }
      }
    }

    def handleMouseDragExited(event: sfxi.MouseEvent): Unit = {
    }

    def handleMouseReleased(event: sfxi.MouseEvent): Unit = {
      world.ship = shipMovement.move()
      shipMovement = new ShipMovement(world.ship)
    }

    def handleMouseMove(event: sfxi.MouseEvent): Unit = {
      displayMouse(event)

      val cursor = point(event)
      cursorTarget = console.pick(cursor)

      cursorTarget match  {
        case Some(_) => displayTarget(cursorTarget)
        case _ => displayTarget(shipTarget)
      }

    }

    def handleMousePressed(event: sfxi.MouseEvent): Unit = {
      val mouse = world(event)

      if (event.isSecondaryButtonDown) {
        shipMovement.move(mouse)
      } else if (event.isPrimaryButtonDown) {
        shipTarget = console.pick(point(event))
      }
      displayTarget(shipTarget)
    }

    def handleMouseClicked(event: sfxi.MouseEvent): Unit = {
    }

    def handleMouseExit(event: sfxi.MouseEvent): Unit = {
    }

    def handleKeyPressed(event: sfxi.KeyEvent): Unit = {
      event.consume()
      val code = event.code
      val direction = getDirection(code)
      code match {
        case Space => fire()
        case Escape => exit()
        case _ =>
      }
    }

    def getDirection(code: KeyCode): Option[Vect] = {
      import KeyCode._
      code match {
        case W | Up | Numpad8 => Option(Vect.Up)
        case S | Down | Numpad2 => Option(Vect.Down)
        case A | Left | Numpad4 => Option(Vect.Left)
        case D | Right | Numpad6 => Option(Vect.Right)
        case _ => None
      }
    }

    def point(event: sfxi.MouseEvent): Point = Point(event.x, event.y)

    def world(event: sfxi.MouseEvent): Point = {
      implicit val origin = world.ship.position
      transform.world(point(event))
    }

    def displayMouse(event: sfxi.MouseEvent): Unit = {
      val mouseScreen = point(event)
      val mouseWorld = world(event)
      mouseScreenLabel.setText(formatIntPoint(mouseScreen))
      mouseWorldLabel.setText(formatPoint(mouseWorld))
    }

    def displayTarget(t: Option[Target]): Unit = {
      def displayEmptyTarget(): Unit = {
        targetLabel.setText("None")
        targetTypeLabel.setText("")
        targetPositionLabel.setText("")
        targetDistanceLabel.setText("")
      }

      def displayTarget(t: Target): Unit = {
        targetLabel.setText(t.name)
        val targetType = t match {
          case _:Planet => "Planet"
          case _:Star => "Star"
          case _:Ship => "Ship"
          case _ => "Unknown"
        }
        targetTypeLabel.setText(targetType)
        targetPositionLabel.setText(formatPoint(t.position))
        val distance = (t.position - world.ship.position).normal
        targetDistanceLabel.setText(formatDouble(distance))
      }

      t match {
        case Some(b: Target) => displayTarget(b)
        case _ => displayEmptyTarget()
      }
    }

    def fire(): Unit = {
      def fireWeapon(weapon: Weapon, ship: Ship): Unit = {
        (1 to weapon.attack).foreach( _ => if (Random.nextBoolean()) { ship.damage(1) })
      }

      def fire(ship: Ship): Unit = {
        world.ship.weapons.foreach(fireWeapon(_, ship))
//        console.fireTorpedo(ship.position, world.ship.position)
        console.firePhaser(ship.position, world.ship.position)
      }

      target match {
        case Some(s: Ship) => fire(s)
        case _ =>
      }
    }

    def formatDouble(value: Double): String = f"$value%.2f"
    def formatPoint(p: Point): String = f"(${p.x}%.2f, ${p.y}%.2f)"
    def formatIntPoint(p: Point): String = s"(${p.x.toInt}, ${p.y.toInt})"
//      f"(${p.x.toInt}%d, ${p.y.toInt}%d)"

  }
}
