package app

import java.awt.{Font, GraphicsEnvironment}
import javafx.application.Application
import javafx.stage.Stage

import controller.Controller
import model.WorldBuilder


class ConsoleApp extends Application {
	override def start(primaryStage: Stage) {
		primaryStage.setTitle("Flagship - Zero")

		object Controller extends Controller {
			lazy val initialState: ControllerState = new GameController(WorldBuilder())
			lazy val stage = primaryStage
		}

		Controller.stage.show()
	}
}


object ConsoleApp extends App {
	Application.launch(classOf[ConsoleApp], args: _*)
}