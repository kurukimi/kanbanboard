package controllers

import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane}
import javafx.scene.layout.{AnchorPane, GridPane}

import java.net.URL
import java.util


class MainController extends jfxf.Initializable{

  @jfxf.FXML
  var TableVController: TableController = _



  @jfxf.FXML
  private def handleListAdd(event: jfxe.ActionEvent): Unit = {

    TableVController.addList()


  }

  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {


  }


}
