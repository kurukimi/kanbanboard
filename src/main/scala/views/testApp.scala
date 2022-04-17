package views

import javafx.{fxml => jfxf, scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

import java.io.IOException


object FXMLMainView extends JFXApp3 {

  def start(): Unit = {


    val resource = getClass.getResource("MainView.fxml")
    if (resource == null) {
      throw new IOException("Cannot load resource")
    }


    val root: jfxs.Parent = jfxf.FXMLLoader.load(resource)

    stage = new PrimaryStage() {
      title = "FXML Demo"
      scene = new Scene(root)
    }
  }


}
