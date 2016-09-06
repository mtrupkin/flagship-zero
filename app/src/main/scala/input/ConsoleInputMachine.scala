package input

import controller.GameController
import model.Target
import org.mtrupkin.math.{Point, Vect}
import org.mtrupkin.state.StateMachine

import scalafx.scene.input.KeyCode
import scalafx.scene.{input => sfxi}

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait ConsoleInput {
  def mouseDragged(event: sfxi.MouseEvent): Unit = {}
  def mouseDragExited(event: sfxi.MouseEvent): Unit = {}
  def mouseReleased(event: sfxi.MouseEvent): Unit = {}
  def mouseMove(event: sfxi.MouseEvent): Unit = {}
  def mousePressed(event: sfxi.MouseEvent): Unit = {}
  def mouseClicked(event: sfxi.MouseEvent): Unit = {}
  def mouseExit(event: sfxi.MouseEvent): Unit = {}
  def keyPressed(event: sfxi.KeyEvent): Unit = {}
}

class ConsoleInputMachine(val controller: GameController) extends StateMachine with ConsoleInput
  with PlayerTurn
  with AnimationWait {

  type StateType = ConsoleInputState
  lazy val initialState: ConsoleInputState = new PlayerTurnState()

  // event utilities
  def toPoint(event: sfxi.MouseEvent): Point = Point(event.x, event.y)
  def toWorld(event: sfxi.MouseEvent): Point = controller.world(toPoint(event))
  def pick(event: sfxi.MouseEvent): Option[Target] = controller.pick(toPoint(event))

  trait ConsoleInputState extends State with ConsoleInput {
    import controller._
    override def mouseMove(event: sfxi.MouseEvent): Unit = {
      val cursor = toPoint(event)
      displayMouse(cursor)
      displayTarget(cursor)
    }
  }

  def direction(code: KeyCode): Option[Vect] = {
    import KeyCode._
    code match {
      case W | Up | Numpad8 => Option(Vect.Up)
      case S | Down | Numpad2 => Option(Vect.Down)
      case A | Left | Numpad4 => Option(Vect.Left)
      case D | Right | Numpad6 => Option(Vect.Right)
      case _ => None
    }
  }

  override def mouseMove(event: sfxi.MouseEvent): Unit = currentState.mouseMove(event)
  override def keyPressed(event: sfxi.KeyEvent): Unit =  currentState.keyPressed(event)
  override def mousePressed(event: sfxi.MouseEvent): Unit = currentState.mousePressed(event)
  override def mouseReleased(event: sfxi.MouseEvent): Unit = currentState.mouseReleased(event)
  override def mouseClicked(event: sfxi.MouseEvent): Unit = currentState.mouseClicked(event)
}
