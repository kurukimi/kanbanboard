package controllers


import Service.KanbanService
import javafx.fxml.FXMLLoader
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import javafx.scene.text.Text
import javafx.scene.control.{Alert, MenuItem, TextArea, TextInputDialog}
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.{ListChangeListener, SetChangeListener}
import javafx.event.{ActionEvent, Event}
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.stage.DirectoryChooser
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
    var d = directoryChooser.showDialog(cardV.getScene.getWindow)
    if (d != null) {
       val success = KanbanService.saveCard(cardV.card, d.getPath)
        if (!success) {val al = new Alert(AlertType.ERROR); al.setContentText("File save failed"); al.show()}
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
    val inp = new TextInputDialog()
    inp.setContentText("tag name")
    var di = inp.showAndWait()
    di.ifPresent(tag => {
      cardV.card.addTags(tag)
    })
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    timeT.setText(s"${cardV.card.getTimeElapsed} hours ago")
    textT.setText(cardV.card.getText)
    cardV.card.getTags.foreach(z => {val mn = new MenuItem();mn.setText("@"+z) ;tags.getItems.add(mn)})
    textT.textProperty.addListener(new ChangeListener[String]() {
      override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit = {
        cardV.card.setText(t1)
      }})

    cardV.card.getObsTags.addListener(new SetChangeListener[String]() {
      override def onChanged(change: SetChangeListener.Change[_ <: String]): Unit = {
        tags.getItems.clear()
        cardV.card.getTags.foreach(z => {
          val m = new MenuItem()
          m.setText("@" + z)
          m.setOnAction(e => {cardV.card.removeTags(z)})
          tags.getItems.add(m)

        })
      }
    })

    cardV.setOnDragDetected( event => {
      val im = cardV.snapshot(null, null)
      var db = cardV.startDragAndDrop(TransferMode.MOVE)
      var content = new ClipboardContent()
      content.putString("")
      db.setContent(content)
      db.setDragView(im)
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