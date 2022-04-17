package controllers


import Service.KanbanService
import javafx.fxml.FXMLLoader
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane}
import javafx.scene.layout.GridPane
import scalafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.control.TextArea

import java.io.IOException
import java.net.URL
import java.util

class CardController extends jfxf.Initializable{
  @jfxf.FXML
  var textT: TextArea = _
  @jfxf.FXML
  var timeT: Text = _
  @jfxf.FXML
  var cardV: CardView = _

  @jfxf.FXML
  def addText() = {


  }

  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    timeT.setText(s"${cardV.card.getTimeElapsed} hours ago")


  }

}

class CardView(val card: Models.Card) extends jfxsl.VBox {

    try {
      var loader = new FXMLLoader(getClass.getResource("/views/CardView.fxml"))
      loader.setRoot(this)
      loader.load()
    } catch {
      case e: IOException => throw e
    }
}