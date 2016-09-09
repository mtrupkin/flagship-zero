package input

import controller.Game

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait WaitInputMachine { self: Game =>
  trait AnimationWait { self: GameControllerState =>
    class AnimationWaitState(val old: ConsoleInputState) extends ConsoleInputState {
      override def onEnter() = println("wait")
      def finished(): Unit = {
        println("wait finished")
        changeState(old)
      }
    }
  }
}