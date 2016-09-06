package input

import model.{Ship, Target}
import org.mtrupkin.math.Point

import scalafx.scene.input.KeyCode._
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.{input => sfxi}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait PlayerTurn { self: ConsoleInputMachine =>
  class PlayerTurnState extends ConsoleInputState {

    def fire(target: Option[Target]): Unit = {
      target match {
        case Some(ship: Ship) => {
          val newState = new AnimationWaitState(this)

          val future = controller.fire(controller.world.ship, ship)
          future.onSuccess { case _ => newState.finished() }

          changeState(newState)
        }
        case _ =>
      }
    }

    def move(p: Point): Unit = {
      val newState = new AnimationWaitState(this)
      val future = controller.move(controller.world.ship, p)
      future.onSuccess { case _ => newState.finished() }
      changeState(newState)
    }

    override def keyPressed(event: sfxi.KeyEvent): Unit = {
      event.consume()
      val code = event.code
      code match {
        case Space => fire(controller.target)
        case Escape => controller.escape()
        case _ =>
      }
    }

    def mousePrimaryPressed(event: MouseEvent): Unit = {
      val target = pick(event)
      fire(target)
    }

    def mouseSecondaryPressed(event: MouseEvent): Unit = {
      val target = toWorld(event)
      move(target)
    }

    override def mouseMove(event: MouseEvent): Unit = {
      controller.console.displayMove(controller.world.ship, toWorld(event))
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
