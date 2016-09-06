package controller

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.Pane

import console.Transform
import control.GameConsole
import input.ConsoleInputMachine
import model._
import org.mtrupkin.math.{Point, Size, Vect}

import scala.collection.mutable
import scala.concurrent.Future
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyCode._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.application.Platform

class FiniteQueue(maxSize: Int) extends mutable.Queue[Double] {
  def enqueueFinite(elem: Double): Unit = {
    enqueue(elem)
    while (size > maxSize) { dequeue }
  }
}

trait GameController {
  def world: World
  def target: Option[Target]
  def console: GameConsole

  def fire(source: Ship, destination: Ship): Future[Unit]
  // world coordinates
  def move(source: Ship, p: Point): Future[Unit]


  // screen coordinates
  def pick(p: Point): Option[Target]
  // screen to world coordinates
  def world(p: Point): Point
  // screen coordinates
  def displayMouse(p: Point): Unit
  // screen coordinates
  def displayTarget(p: Point): Unit

  def escape(): Unit
}

trait Game { self: Controller =>
  class GameControllerState(val world: World) extends ControllerState with GameController {
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
    @FXML var fpsLabel: Label = _

    val screenSize = Size(720, 720)
    //  // world unit size is one sprite unit
    //  // world size (90, 90)
    val worldSize = screenSize / spriteset.SPRITE_UNIT_PIXEL
    val transform = Transform(screenSize, worldSize)

    val console = GameConsole(transform)
    val consoleInput = new ConsoleInputMachine(this)

    def initialize(): Unit = {
      {
        import console._
        import consoleInput._

        onMouseClicked = (e: sfxi.MouseEvent) => mouseClicked(e)
        onMousePressed = (e: sfxi.MouseEvent) => mousePressed(e)
        onMouseMoved = (e: sfxi.MouseEvent) => mouseMove(e)
        onMouseExited = (e: sfxi.MouseEvent) => mouseExit(e)
        onMouseDragged = (e: sfxi.MouseEvent) => mouseDragged(e)
        onMouseDragExited = (e: sfxi.MouseEvent) => mouseDragExited(e)
        onMouseReleased = (e: sfxi.MouseEvent) => mouseReleased(e)

        new sfxl.Pane(consolePane) {
          filterEvent(sfxi.KeyEvent.KeyPressed) {
            (event: sfxi.KeyEvent) => keyPressed(event)
          }
        }
      }

      consolePane.getChildren.clear()
      consolePane.getChildren.add(console)

      consolePane.setFocusTraversable(true)

      // initialize console
      {
        world.entities.foreach(console.add(_))
        console.add(world.ship)
      }
    }

    val fpsQueue = new FiniteQueue(10000)

    override def update(elapsed: Int): Unit = {
      console.update()
//      cursorTarget.foreach(console.cursor(_))
//      shipTarget.foreach(console.target(_))

      fpsQueue.enqueueFinite(10000.0/elapsed)

      val fps = (fpsQueue.sum / fpsQueue.size).toInt

      fpsLabel.setText(s"$fps")
    }

    def handleMouseReleased(event: sfxi.MouseEvent): Unit = {
      shipMovement.move()
      shipMovement = new ShipMovement(world.ship)
    }

    def world(p: Point): Point = {
      implicit val origin = world.ship.position
      transform.world(p)
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

    def pick(p: Point): Option[Target] = console.pick(p)

    def move(source: Ship, p: Point): Future[Unit] = {
      console.move(source, p).map( _ => {
        source.position = p
      })
    }

    def fire(source: Ship, destination: Ship): Future[Unit] = {
      def fireWeapon(weapon: Weapon, ship: Ship): Unit = {
        ship.damage(Combat.attack(weapon.rating))
      }


      source.weapons.foreach(fireWeapon(_, destination))
//        console.fireTorpedo(ship.position, world.ship.position)
      console.firePhaser(destination.position, source.position).map( _ => {
        if (destination.shields < 0) {
          console.destroy(destination).map(_ => {
            shipTarget = None
            cursorTarget = None
            Platform.runLater(displayTarget(shipTarget))
          })
        }
      } )
    }

    def displayMouse(mouseScreen: Point): Unit = {
      val mouseWorld = world(mouseScreen)
      mouseScreenLabel.setText(formatIntPoint(mouseScreen))
      mouseWorldLabel.setText(formatPoint(mouseWorld))
    }

    def displayTarget(cursor: Point): Unit = {
      cursorTarget = console.pick(cursor)

      cursorTarget match  {
        case Some(_) => displayTarget(cursorTarget)
        case _ => displayTarget(shipTarget)
      }
    }

    def escape(): Unit = exit()

    def formatDouble(value: Double): String = f"$value%.2f"
    def formatPoint(p: Point): String = f"(${p.x}%.2f, ${p.y}%.2f)"
    def formatIntPoint(p: Point): String = s"(${p.x.toInt}, ${p.y.toInt})"
  }
}
