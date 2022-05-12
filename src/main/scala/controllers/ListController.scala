package controllers

import Models.Card
import Service.KanbanService
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ListChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.{Node, control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import javafx.scene.layout.{GridPane, Priority, VBox}
import java.io.IOException
import java.net.URL
import java.util


class ListController extends jfxf.Initializable{
  @jfxf.FXML
  var ListV: ListView = _
  @jfxf.FXML
  var title: jfxsc.TextField = _
  @jfxf.FXML
  var ListA: VBox = _
  @jfxf.FXML
  var ListB: VBox = _




  @jfxf.FXML
  def addCard() = {
    val c = KanbanService.addCard("" , ListV.listModel)
  }

   @jfxf.FXML
  def delList() = {
    ListV.table.delList(ListV)
  }

  def update() = {
    val cards = KanbanService.getCards(ListV.listModel).map(new CardView(_, this))
    while (cards.length < ListA.getChildren.length) {
      ListA.getChildren.remove(ListA.getChildren.indexOf(ListA.getChildren.last))
    }
    for (i <- cards.indices) {
      val cardView = cards(i)
      val maybeCardView = ListA.getChildren.toBuffer.lift(i)
      maybeCardView match {
        case Some(cView: CardView) => {
          if (cView.card != cardView.card) {
          ListA.getChildren.set(i, cardView)
        } }
        case _ => {
          if (i > ListA.getChildren.length-1) ListA.getChildren.add(i, cardView)
          else ListA.getChildren.set(i, cardView)
        }
      }
    }
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    title.setText(ListV.listModel.name)
    GridPane.setHgrow(ListV, Priority.ALWAYS)

    ListV.listModel.getObs.delegate.addListener(new ListChangeListener[Models.Card]() {
      override def onChanged(change: ListChangeListener.Change[_ <: Card]): Unit = update()
    })

    ListV.setOnDragDetected( event => {
        val image = ListV.snapshot(null, null)
        var dragboard = ListV.startDragAndDrop(TransferMode.MOVE)
        var content = new ClipboardContent()
        content.putString("")
        dragboard.setContent(content)
        dragboard.setDragView(image)
        event.consume()
    })

    ListV.setOnDragEntered(event => {
      val source = event.getGestureSource match {
        case listView: ListView => {
          if (ListV != listView){
              ListV.setRotate(3)
        }}
        case cardV: CardView => {
            if (!ListV.listModel.getCards.contains(cardV.card)) {
              ListV.setRotate(-3)
            }
          }
      }})

    ListV.setOnDragExited(e =>{
      if (ListV != e.getGestureSource){
        ListV.setRotate(0)
    }}
    )

    ListV.setOnDragDropped(e => {
      val target =  e.getGestureTarget match {
        case listView: ListView => listView
      }

     val source = e.getGestureSource match {
        case listView: ListView => {
                val lists = KanbanService.getListObs(ListV.table.tablePane.board)
                val targIndx = lists.indexOf(target.listModel)
                val srcIndx = lists.indexOf(listView.listModel)
                lists.update(targIndx, listView.listModel)
                lists.update(srcIndx, target.listModel)
        }
        case cardView: CardView => {
          target.listModel.addCard(cardView.card)
          cardView.listIn.ListV.listModel.removeCard(cardView.card)
        }
      }
      target.setRotate(0)
      e.setDropCompleted(true)
      e.consume()
    })

    ListV.setOnDragOver(ev => {
      ev.acceptTransferModes(TransferMode.ANY)
      ev.consume()
    })

    title.textProperty.addListener(new ChangeListener[String]() {
      override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit = {
        ListV.listModel.setName(t1)
      }
      }
    )
  }
}



class ListView(val listModel: Models.CardList, val table: TableController) extends jfxsl.VBox{
    try {
      var loader = new FXMLLoader(getClass.getResource("/views/ListView.fxml"))
      loader.setRoot(this)
      loader.load()
    } catch {
      case e: IOException => throw e
    }

}
