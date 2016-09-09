package input


import controller.Game
import org.mtrupkin.math.{Point, Vect}
import org.mtrupkin.state.StateMachine

import scalafx.scene.{input => sfxi}
trait ConsoleInput {
  def mouseDragged(event: sfxi.MouseEvent): Unit
  def mouseDragExited(event: sfxi.MouseEvent): Unit
  def mouseReleased(event: sfxi.MouseEvent): Unit
  def mouseMove(event: sfxi.MouseEvent): Unit
  def mousePressed(event: sfxi.MouseEvent): Unit
  def mouseClicked(event: sfxi.MouseEvent): Unit
  def mouseExit(event: sfxi.MouseEvent): Unit
  def keyPressed(event: sfxi.KeyEvent): Unit
}

trait InputMachine extends PlayerInputMachine with WaitInputMachine {
  self: Game =>
  trait GameInputMachine extends StateMachine
    with PlayerTurn
    with AnimationWait {
    self: GameControllerState =>

    type StateType = ConsoleInputState
    lazy val initialState: ConsoleInputState = new PlayerTurnState()

    // event utilities
    def toPoint(event: sfxi.MouseEvent): Point = Point(event.x, event.y)
    def toWorld(event: sfxi.MouseEvent): Point = world(toPoint(event))

    trait ConsoleInputState extends State with ConsoleInput {
      def mouseDragged(event: sfxi.MouseEvent): Unit = {}
      def mouseDragExited(event: sfxi.MouseEvent): Unit = {}
      def mouseReleased(event: sfxi.MouseEvent): Unit = {}
      def mouseMove(event: sfxi.MouseEvent): Unit = {
        val cursor = toPoint(event)
        displayMouse(cursor)
        cursorTarget = pick(cursor)
        displayTarget(cursorTarget)
      }
      def mousePressed(event: sfxi.MouseEvent): Unit = {}
      def mouseClicked(event: sfxi.MouseEvent): Unit = {}
      def mouseExit(event: sfxi.MouseEvent): Unit = {}
      def keyPressed(event: sfxi.KeyEvent): Unit = {}
    }

    def mouseDragged(event: sfxi.MouseEvent): Unit = currentState.mouseDragged(event)
    def mouseDragExited(event: sfxi.MouseEvent): Unit = currentState.mouseDragExited(event)
    def mouseReleased(event: sfxi.MouseEvent): Unit = currentState.mouseReleased(event)
    def mouseMove(event: sfxi.MouseEvent): Unit = currentState.mouseMove(event)
    def mousePressed(event: sfxi.MouseEvent): Unit = currentState.mousePressed(event)
    def mouseClicked(event: sfxi.MouseEvent): Unit = currentState.mouseClicked(event)
    def mouseExit(event: sfxi.MouseEvent): Unit = currentState.mouseExit(event)
    def keyPressed(event: sfxi.KeyEvent): Unit = currentState.keyPressed(event)
  }
}