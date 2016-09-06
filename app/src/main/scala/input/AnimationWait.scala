package input

/**
  * Created by mtrupkin on 9/1/2016.
  */
trait AnimationWait {
  self: ConsoleInputMachine =>
  class AnimationWaitState(val old: ConsoleInputState) extends ConsoleInputState {
      def finished(): Unit = changeState(old)
  }
}