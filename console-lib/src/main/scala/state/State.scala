package org.mtrupkin.state

trait StateMachine {
  type StateType <: State

  def initialState: StateType

  private var _currentState = initialState
  def currentState: StateType = _currentState

  trait State {
    def onEnter(): Unit = {}
    def onExit(): Unit = {}

    def changeState(newState: StateType): Unit = {
      currentState.onExit()
      _currentState = newState
      currentState.onEnter()
    }
  }
}
