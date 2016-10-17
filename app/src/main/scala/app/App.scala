package app

import java.awt.{Font, GraphicsEnvironment}
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

import controller.Controller
import model.World


class ConsoleApp extends Application {
	override def start(primaryStage: Stage) {
		val icon = getClass.getResourceAsStream("/icons/app.png")
		primaryStage.setTitle("Flagship Zero")
		primaryStage.getIcons.add(new Image(icon))

		object Controller extends Controller {
			lazy val initialState: ControllerState = new GameControllerState(World())
			lazy val stage = primaryStage
		}

		Controller.stage.show()
	}
}


object ConsoleApp extends App {
	Application.launch(classOf[ConsoleApp], args: _*)
}