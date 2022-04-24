package controllers


import Models.Card
import Service.KanbanService
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ListChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextInputDialog
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.{Node, control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane}
import javafx.scene.layout.{GridPane, Priority, VBox}
import javafx.scene.text.Text
import scalafx.scene.image.ImageView

import java.io.IOException
import java.net.URL
import java.util
import java.util.ResourceBundle
import scala.concurrent.Future

class ListController extends jfxf.Initializable{
  @jfxf.FXML
  var ListV: ListView = _
  @jfxf.FXML
  var title: jfxsc.TextField = _
  @jfxf.FXML
  var ListA: VBox = _
  @jfxf.FXML
  var ListB: VBox = _

  var initial = true


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
      val e = cards(i)
      val c = ListA.getChildren.toBuffer.lift(i)
      c match {
        case Some(x: CardView) => {
          if (x.card != e.card) {
          ListA.getChildren.set(i, e)
        } }
        case _ => {
          if (i > ListA.getChildren.length-1) ListA.getChildren.add(i, e)
          else ListA.getChildren.set(i, e)
        }
      }

    }

  }






  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {

    title.setText(ListV.listModel.name)
    GridPane.setHgrow(ListV, Priority.ALWAYS)
    if (initial) {
    ListV.listModel.getObs.delegate.addListener(new ListChangeListener[Models.Card]() {
      override def onChanged(change: ListChangeListener.Change[_ <: Card]): Unit = {update()}

    })
      initial = false
    }

    ListV.setOnDragDetected( event => {
        val im = ListV.snapshot(null, null)

        var db = ListV.startDragAndDrop(TransferMode.MOVE)
        var content = new ClipboardContent()
        content.putString("")
        db.setContent(content)
        db.setDragView(im)

        event.consume()

    })

    ListV.setOnDragEntered(e =>{
      val s = e.getGestureSource match {
        case x: ListView => {
          if (ListV != x){
              ListV.setRotate(3)

        }}
        case z: CardView => {
            if (!ListV.listModel.getCards.contains(z.card)) {
              ListV.setRotate(-3)
            }

          }
      }

    }
    )
    ListV.setOnDragExited(e =>{
      if (ListV != e.getGestureSource){

        ListV.setRotate(0)
    }}
    )


    ListV.setOnDragDropped(e => {
      val target =  e.getGestureTarget match {
        case x: ListView => x
      }


     val source = e.getGestureSource match {
        case x: ListView => {
                val lsts = KanbanService.getListObs(ListV.table.tablePane.b)
                val targIndx = lsts.indexOf(target.listModel)
                val srcIndx = lsts.indexOf(x.listModel)
                lsts.update(targIndx, x.listModel)
                lsts.update(srcIndx, target.listModel)
        }
        case z: CardView => {
          target.listModel.addCard(z.card)
          z.listIn.ListV.listModel.removeCard(z.card)

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
