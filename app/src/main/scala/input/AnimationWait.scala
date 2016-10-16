package input

import controller.Game

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait WaitInputMachine { self: Game =>
  trait PlayerWait { self: GameControllerState =>
    class PlayerWaitState(val old: ConsoleInputState) extends ConsoleInputState {
      var total: Int = _
      override def onEnter(): Unit = {
        total = 0
      }
      def finished(): Unit = changeState(old)
    }
  }
}