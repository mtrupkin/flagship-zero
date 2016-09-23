package input

import controller.Game
import model.{Ship, Target}
import org.mtrupkin.math.Point

import scalafx.scene.input.KeyCode._
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.{input => sfxi}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait PlayerInputMachine { self: Game =>
  trait PlayerTurn { self: GameControllerState =>
    class PlayerTurnState extends ConsoleInputState {
      def performFire(target: Option[Target]): Unit = {
        target match {
          case Some(ship: Ship) => {
            val newState = new AnimationWaitState(this)

            val future = fire(ship)
            future.onSuccess { case _ => newState.finished() }

            changeState(newState)
          }
          case _ =>
        }
      }

      def performMove(p: Point): Unit = {
        val newState = new AnimationWaitState(this)
        val future = move(world.ship, p)
        future.onSuccess { case _ => newState.finished() }
        changeState(newState)
      }

      override def keyPressed(event: sfxi.KeyEvent): Unit = {
        super.keyPressed(event)
        event.consume()
        val code = event.code
        code match {
          case Space => performFire(target)
          case Escape => escape()
          case _ =>
        }
      }

      def mousePrimaryPressed(event: MouseEvent): Unit = {
        val target = pick(toPoint(event))
        performFire(target)
      }

      def mouseSecondaryPressed(event: MouseEvent): Unit = {
        val target = toWorld(event)
        performMove(target)
      }

      override def mouseDragged(event: MouseEvent): Unit = {
        if (event.secondaryButtonDown) displayMove(toWorld(event))
      }

      override def mousePressed(event: MouseEvent): Unit = {
        if (event.secondaryButtonDown) displayMove(toWorld(event))
      }

      override def mouseClicked(event: MouseEvent): Unit = {
        event.button match {
          case MouseButton.Primary => mousePrimaryPressed(event)
          case MouseButton.Secondary => mouseSecondaryPressed(event)
          case _ =>
        }
      }
    }
  }
}