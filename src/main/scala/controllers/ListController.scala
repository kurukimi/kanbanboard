package controllers


import Service.KanbanService
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextInputDialog
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane}
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import scalafx.scene.layout.VBox

import java.io.IOException
import java.net.URL
import java.util

class ListController extends jfxf.Initializable{
  @jfxf.FXML
  var ListV: ListView = _
  @jfxf.FXML
  var title: Text = _

  @jfxf.FXML
  def addCard() = {


    val c = KanbanService.addCard("" , ListV.listModel)
    ListV.getChildren.add(new CardView(c))



  }
/*
  def apply() = {
    ListV.getChildren.clear()
    val cards = KanbanService.getCards(ListV.listModel)
    ListV.getChildren.add(new CardView(cards.last))

  }**/

  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    title.setText(ListV.listModel.name)

  }

}

class ListView(val listModel: Models.CardList) extends jfxsl.VBox {

    try {
      var loader = new FXMLLoader(getClass.getResource("/views/ListView.fxml"))
      loader.setRoot(this)
      loader.load()
    } catch {
      case e: IOException => println(e)
    }
}
