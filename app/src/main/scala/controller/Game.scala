package controller

import javafx.fxml.FXML
import javafx.scene.layout.Pane

import model.{Sprite, World}
import org.mtrupkin.control.{ConsoleFx, CoordinateConverter}
import org.mtrupkin.math.{Point, Size, Vect}

import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyCode._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import spriteset._

trait Game { self: Controller =>
  class GameController(var world: World) extends ControllerState {
    val name = "Game"

    var shipMovement = new ShipMovement(world.ship)

    @FXML var consolePane: Pane = _
    val screenSize = Size(800, 800)
    // world unit size is one sprite unit
    val worldSize = screenSize / SPRITE_UNIT_PIXEL
    val converter = CoordinateConverter(screenSize, worldSize)

    val console = ConsoleFx(screenSize, converter)


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
      console.clear()
      world.background.foreach(b => console.drawSprite(b._1, b._2))
      val sprite = Sprite(world.ship.copy(heading = shipMovement.heading))
      console.drawSprite(world.ship.position, sprite)

      console.drawVect(world.ship.position, shipMovement.heading)
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
    }

    def handleMousePressed(event: sfxi.MouseEvent): Unit = {
      if (event.isSecondaryButtonDown) {
        val cursor = toWorld(event)
        shipMovement.move(cursor)
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

    def toWorld(event: sfxi.MouseEvent): Point = converter.toWorld(Point(event.x, event.y))
  }
}


