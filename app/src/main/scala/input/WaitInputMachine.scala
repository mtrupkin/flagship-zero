package input

import controller.Game

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait WaitInputMachine { self: Game =>
  trait PlayerWait { self: GameControllerState =>
    class PlayerWaitState(val oldState: ConsoleInputState) extends ConsoleInputState {
      override def onEnter(): Unit = println("player wait")
      def finished(): Unit = {
        println("player wait finished")
        if (player.power <= 0) {
          player.power = player.maxPower
          world.activate(finished)
        } else changeState(oldState)
      }
    }
  }
}