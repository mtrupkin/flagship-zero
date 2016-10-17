package controller

import javafx.fxml.FXML
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{Label, TableColumn, TableView, TitledPane}
import javafx.scene.layout.{HBox, Pane}

import console.Transform
import control.GameConsole
import input._
import model._
import org.mtrupkin.math.{Point, Size, Vect}

import scala.collection.mutable
import scala.concurrent.Future
import scalafx.Includes._
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.SelectionMode

class FiniteQueue(maxSize: Int) extends mutable.Queue[Double] {
  def enqueueFinite(elem: Double): Unit = {
    enqueue(elem)
    while (size > maxSize) { dequeue }
  }
}


trait Game extends InputMachine {
  self: Controller =>
  class GameControllerState(val world: World) extends ControllerState
    with GameInputMachine {
    val name = "Game"

    var shipTarget: Option[Target] = None
    var cursorTarget: Option[Target] = None

    def target: Option[Target] = shipTarget.orElse(cursorTarget)

    @FXML var rootPane: Pane = _
    @FXML var consolePane: Pane = _
    @FXML var targetLabel: Label = _
    @FXML var targetTypeLabel: Label = _
    @FXML var targetPositionLabel: Label = _
    @FXML var targetDistanceLabel: Label = _
    @FXML var mouseWorldLabel: Label = _
    @FXML var mouseScreenLabel: Label = _
    @FXML var fpsLabel: Label = _
    @FXML var weaponsPane: TitledPane = _
    @FXML var energyPane: Pane = _
    var energyPaneFX: sfxl.Pane = _

    val screenSize = Size(720, 720)
    //  // world unit size is one sprite unit
    //  // world size (90, 90)
    val worldSize = screenSize / spriteset.SPRITE_UNIT_PIXEL
    val transform = Transform(screenSize, worldSize)

    val console = new GameConsole(transform)
    var weaponsTable: sfxc.TableView[Weapon] = _

    def player: Ship = world.player

    def initialize(): Unit = {
      {
        import console._

        onMouseClicked = (e: sfxi.MouseEvent) => mouseClicked(e)
        onMousePressed = (e: sfxi.MouseEvent) => mousePressed(e)
        onMouseMoved = (e: sfxi.MouseEvent) => mouseMove(e)
        onMouseExited = (e: sfxi.MouseEvent) => mouseExit(e)
        onMouseDragged = (e: sfxi.MouseEvent) => mouseDragged(e)
        onMouseDragExited = (e: sfxi.MouseEvent) => mouseDragExited(e)
        onMouseReleased = (e: sfxi.MouseEvent) => mouseReleased(e)

        new sfxl.Pane(rootPane) {
          filterEvent(sfxi.KeyEvent.KeyPressed) {
            (event: sfxi.KeyEvent) => keyPressed(event)
          }
        }
      }

      val weapons = ObservableBuffer[Weapon]()
      weapons.appendAll(player.weapons)

      val col1 =  new sfxc.TableColumn[Weapon, String] {
        text = "Name"
        cellValueFactory = {_.value.nameProp}
        prefWidth = 200
      }

      val col2 = new sfxc.TableColumn[Weapon, Int] {
        text = "Rating"
        cellValueFactory = {_.value.ratingProp}
        prefWidth = 200
      }

      weaponsTable = new sfxc.TableView[Weapon](weapons) {
        columns ++= List(col1, col2)
        prefHeight = 200
        selectionModel().selectionMode = SelectionMode.Multiple
      }

      weaponsPane.content = weaponsTable

      energyPaneFX = new sfxl.Pane(energyPane)

      consolePane.getChildren.clear()
      consolePane.getChildren.add(console)

      consolePane.setFocusTraversable(true)

      actionUpdate()
    }

    def actionUpdate(): Unit = {
      def powerUpdate(): Unit = {
        val powerButtons = (1 to world.ship.maxPower).map(i => {
          new sfxc.Button(s"$i") {
            disable = (i > world.ship.power)
          }
        })
        energyPaneFX.children = powerButtons
      }

    override def update(elapsed: Int): Unit = {
      world.update(elapsed)
      console.update(elapsed, world.entities)

      fpsQueue.enqueueFinite(10000.0/elapsed)
      val fps = (fpsQueue.sum / fpsQueue.size).toInt
      fpsLabel.setText(s"$fps")
      powerUpdate()
    }

    val fpsQueue = new FiniteQueue(10000)

    override def update(elapsed: Int): Unit = {
      def fpsUpdate(): Unit = {
        fpsQueue.enqueueFinite(10000.0/elapsed)
        val fps = (fpsQueue.sum / fpsQueue.size).toInt
        fpsLabel.setText(s"$fps")
      }

      world.update(elapsed)
      console.update()
    }

    def world(p: Point): Point = {
      implicit val origin = player.position
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
        val distance = (t.position - player.position).normal
        targetDistanceLabel.setText(formatDouble(distance))
      }

      t match {
        case Some(b: Target) => displayTarget(b)
        case _ => displayEmptyTarget()
      }
    }

    def pick(p: Point): Option[Target] = console.pick(p)

    def move(source: Ship, p1: Point): Future[Unit] = {
      console.movePath.elements.clear()

      val motion = new TieredMotion(player.position, player.heading)
      val v = motion(p1)
      val p = source.position + v
      console.move(source, p).map( _ => {
        val heading = p - source.position
        source.position = p
        source.heading = heading.normalize
      })
    def move(p1: Point): Unit = {
      val p = world.ship.move(p1)
      actionUpdate()
    }


    def displayMove(p : Point): Unit = {
//      val motion = new CircularMotion(world.ship.position, world.ship.heading)
      val motion = new TieredMotion(player.position, player.heading)
      val v = motion(p)
      console.displayLineMove(player.position, v)
    }

    def fire(destination: Ship): Unit = {
      val ship = player

      def fireWeapon(weapon: Weapon): Unit = {
        val range = (destination.position - ship.position).normal
        destination.damage(weapon.attack(range))
        ship.power -= 1
      }

      val weapons = weaponsTable.selectionModel().getSelectedItems.toList
      val animations = weapons.map {
        case torpedo: Torpedo1 => {
          fireWeapon(torpedo)
          world.fire(ship, torpedo, destination)
        }
        case phaser: Phaser1 => {
          fireWeapon(phaser)
          console.firePhaser(destination.position, ship.position)
        }
      }
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
