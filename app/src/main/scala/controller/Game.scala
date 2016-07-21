package controller

import javafx.fxml.FXML
import javafx.scene.control.{Label, Slider, TextArea}
import javafx.scene.layout.Pane

import model.{Tile, World}
import org.mtrupkin.control.ConsoleFx
import org.mtrupkin.math.{Point, Size, Vect}
import tileset.Oryx

import scalafx.Includes._
import scalafx.scene.input.{KeyCode, MouseButton}
import scalafx.scene.input.KeyCode._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}

trait Game { self: Controller =>
  class GameController(var world: World) extends ControllerState {
    val name = "Game"

    var shipMovement = new ShipMovement(world.ship)

    @FXML var consolePane: Pane = _
    val console = ConsoleFx()

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
//      world.objects.foreach(tile => console.draw(tile.position, Size(tile.size, tile.size), tile.imageView))
      val tile = Tile(world.ship)
      console.draw(tile.position, Size(tile.size, tile.size), tile.imageView)

      console.drawVect(world.ship.position, shipMovement.velocity)
    }

    def handleMouseDragged(event: sfxi.MouseEvent): Unit = {
      if (event.isSecondaryButtonDown) {
        val cursor = console.screenToWorld(event.x, event.y)
        shipMovement.move(cursor)
      }
    }

    def handleMouseDragExited(event: sfxi.MouseEvent): Unit = {
      world.ship = world.ship.move(shipMovement.velocity)
    }

    def handleMouseReleased(event: sfxi.MouseEvent): Unit = {
      world.ship = world.ship.move(shipMovement.velocity)
      shipMovement = new ShipMovement(world.ship)
    }

    def handleMouseMove(event: sfxi.MouseEvent): Unit = {
    }

    def handleMousePressed(event: sfxi.MouseEvent): Unit = {
      if (event.isSecondaryButtonDown) {
        val cursor = console.screenToWorld(event.x, event.y)
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
  }
}


