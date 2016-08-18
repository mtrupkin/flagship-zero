package controller

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.Pane

import model.{World, Target}
import org.mtrupkin.control.{ConsoleFx, CoordinateConverter}
import org.mtrupkin.math.{Point, Size, Vect}

import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyCode._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import spriteset._

trait Game { self: Controller =>
  class GameController(val world: World) extends ControllerState {
    val name = "Game"

    var shipMovement = new ShipMovement(world.ship)
    var shipTarget: Option[Target] = None

    @FXML var consolePane: Pane = _
    @FXML var targetLabel: Label = _

    val console = ConsoleFx(world.converter)

    def initialize(): Unit = {
      new sfxl.Pane(consolePane) {
        filterEvent(sfxi.KeyEvent.KeyPressed) {
          (event: sfxi.KeyEvent) => handleKeyPressed(event)
        }
      }

      new sfxl.Pane(console) {
        onMouseClicked = (e: sfxi.MouseEvent) => handleMouseClicked(e)
        onMousePressed = (e: sfxi.MouseEvent) => handleMousePressed(e)
        onMouseMoved = (e: sfxi.MouseEvent) => handleMouseMove(e)
        onMouseExited = (e: sfxi.MouseEvent) => handleMouseExit(e)
        onMouseDragged = (e: sfxi.MouseEvent) => handleMouseDragged(e)
        onMouseDragExited = (e: sfxi.MouseEvent) => handleMouseDragExited(e)
        onMouseReleased = (e: sfxi.MouseEvent) => handleMouseReleased(e)
      }

      consolePane.getChildren.clear()
      consolePane.getChildren.add(console)

      consolePane.setFocusTraversable(true)
    }

    override def update(elapsed: Int): Unit = {
      import world._
      implicit val origin = ship.position


      console.clear()
      entities.foreach(e => console.drawSprite(e.position, e.sprite))
      console.drawSprite(ship.position, ship.copy(heading = shipMovement.heading).sprite)
      console.drawVect(ship.position, shipMovement.heading)
      shipTarget.foreach( t => console.drawTarget(t.position, t.sprite.size))
    }

    def handleMouseDragged(event: sfxi.MouseEvent): Unit = {
      if (event.isSecondaryButtonDown) {
        val cursor = toWorld(event)
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
      val cursor = toWorld(event)
      val cursorTarget = world.target(cursor)

      cursorTarget match  {
        case Some(_) => displayTarget(cursorTarget)
        case _ => displayTarget(shipTarget)
      }

    }

    def handleMousePressed(event: sfxi.MouseEvent): Unit = {
      val cursor = toWorld(event)
      if (event.isSecondaryButtonDown) {
        shipMovement.move(cursor)
      } else if (event.isPrimaryButtonDown) {
        shipTarget = world.target(cursor)
      }
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
        case ESCAPE => exit()
        case _ =>
      }
    }

    def getDirection(code: KeyCode): Option[Vect] = {
      import KeyCode._
      code match {
        case W | UP | NUMPAD8 => Option(Vect.Up)
        case S | DOWN | NUMPAD2 => Option(Vect.Down)
        case A | LEFT | NUMPAD4 => Option(Vect.Left)
        case D | RIGHT | NUMPAD6 => Option(Vect.Right)
        case _ => None
      }
    }

    def toWorld(event: sfxi.MouseEvent): Point = {
      import world._
      implicit val origin = ship.position
      converter.toWorld(Point(event.x, event.y))
    }

    def displayTarget(t: Option[Target]): Unit = {
      val text = t match {
        case Some(b: Target) => b.name
        case _ => "None"
      }
      targetLabel.setText(text)
    }
  }
}


