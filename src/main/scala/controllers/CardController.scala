package controllers

import Service.KanbanService
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.SetChangeListener
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{Alert, MenuItem, TextArea, TextInputDialog}
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.text.Text
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.stage.DirectoryChooser
import javafx.{fxml => jfxf}
import scalafx.Includes._

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
  var menuB: jfxsc.MenuButton = _
  @jfxf.FXML
  var tags: jfxsc.Menu = _

  @jfxf.FXML
  def saveCard() = {
    menuB.hide()
    var directoryChooser = new DirectoryChooser()
    var file = directoryChooser.showDialog(cardV.getScene.getWindow)
    if (file != null) {
       val success = KanbanService.saveCard(cardV.card, file.getPath)
        if (!success) {val alert = new Alert(AlertType.ERROR); alert.setContentText("File save failed"); alert.show()}
    }
  }

  @jfxf.FXML
  def archiveCard() = {
    menuB.hide()
    KanbanService.archiveCard(this.cardV.card, this.cardV.listIn.ListV.listModel)
  }

  @jfxf.FXML
  def removeCard() = {
    menuB.hide()
    KanbanService.removeCard(this.cardV.card, this.cardV.listIn.ListV.listModel)
  }

  @jfxf.FXML
  def addTag(event: ActionEvent) = {
    val inputDialog = new TextInputDialog()
    inputDialog.setContentText("tag name")
    var value = inputDialog.showAndWait()
    value.ifPresent(tag => {
      cardV.card.addTags(tag)
    })
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    timeT.setText(s"${cardV.card.getTimeElapsed} hours ago")
    textT.setText(cardV.card.getText)
    cardV.card.getTags.foreach(str => {val menuItem = new MenuItem();menuItem.setText("@"+str) ;tags.getItems.add(menuItem)})
    textT.textProperty.addListener(new ChangeListener[String]() {
      override def changed(observableValue: ObservableValue[_ <: String], oldStr: String, newStr: String): Unit = {
        cardV.card.setText(newStr)
      }})

    cardV.card.getObsTags.addListener(new SetChangeListener[String]() {
      override def onChanged(change: SetChangeListener.Change[_ <: String]): Unit = {
        tags.getItems.clear()
        cardV.card.getTags.foreach(str => {
          val menuItem = new MenuItem()
          menuItem.setText("@" + str)
          menuItem.setOnAction(e => {cardV.card.removeTags(str)})
          tags.getItems.add(menuItem)

        })
      }
    })

    cardV.setOnDragDetected( event => {
      val image = cardV.snapshot(null, null)
      var dragboard = cardV.startDragAndDrop(TransferMode.MOVE)
      var content = new ClipboardContent()
      content.putString("")
      dragboard.setContent(content)
      dragboard.setDragView(image)
      event.consume()

    })

     cardV.setOnDragOver(ev => {
      ev.acceptTransferModes(TransferMode.ANY)
      ev.consume()
    })
  }
}


class CardView(val card: Models.Card, val listIn: ListController) extends jfxsl.VBox {

    try {
      var loader = new FXMLLoader(getClass.getResource("/views/CardView.fxml"))
      loader.setRoot(this)
      loader.load()
      var cntrl: CardController = loader.getController

    } catch {
      case e: IOException => throw e
    }
}