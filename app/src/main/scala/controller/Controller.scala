package controller


import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import org.mtrupkin.state.StateMachine

import scalafx.animation.AnimationTimer
import scalafx.application.Platform

/**
 * Created by mtrupkin on 12/15/2014.
 */

trait Controller extends StateMachine
with Game {
  type StateType = ControllerState

  def stage: Stage
  val css: String = "/views/Console.css"

  val viewStack = new StackPane()
  viewStack.getChildren.add(new Label("Loading"))
  val scene = new Scene(initialState.root)

  val cssLocation = getClass.getResource(css).toString
  scene.getStylesheets.add(cssLocation)
  stage.setScene(scene)

  var lastPulse: Long = System.nanoTime()

  val timer = AnimationTimer((now: Long) => {
    val elapsed = ((now-lastPulse)/100000).toInt
    currentState.update(elapsed)
    lastPulse = now
  })

  timer.start()

  trait ControllerState extends State {
    def name: String
    def templateName: String = s"/views/$name.fxml"

    def root: Parent = {
      val is = getClass.getResourceAsStream(templateName)
      val loader = new FXMLLoader()

      loader.setController(this)
      loader.load[Parent](is)
    }

    def update(elapsed: Int): Unit = {}

    def exit(): Unit = {
      stage.close()
    }

    override def onEnter(): Unit = {
      // potentially long-running
      Platform.runLater(() => scene.setRoot(root))
    }
  }
}
