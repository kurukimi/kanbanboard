package controllers
import Models.CardList

import scala.jdk.CollectionConverters
import Service.KanbanService
import javafx.scene.control.{ScrollPane, Tab, TextInputDialog}
import javafx.scene.control.skin.ScrollPaneSkin
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.{ColumnConstraints, GridPane, Priority, RowConstraints, StackPane}
import javafx.scene.text.Text
import javafx.scene.{Node, control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, Label, ScrollPane}
import javafx.beans.binding.{Bindings, ListBinding}
import javafx.collections
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.fxml.FXMLLoader

import java.io.IOException
import scala.collection.mutable.Map
import collection.mutable.Buffer
import java.net.URL
import java.util
import scala.jdk.CollectionConverters.BufferHasAsJava



class TableController extends jfxf.Initializable{

  val coordinateLookUp = Map[(Int, Int) , ListView]()
  val tb = this


  @jfxf.FXML
  var tablePane: TableView = _
  @jfxf.FXML
  var ListVController: ListController = _




  def delList(l: ListView): Unit = {
    KanbanService.removeList(l.listModel)(tablePane.b)


  }



  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {

    println(tablePane.b + " c called")
    val lists = KanbanService.getListObs(tablePane.b)


    lists.addListener(new ListChangeListener[Models.CardList] () {
      override def onChanged(c: ListChangeListener.Change[_ <: CardList]): Unit = {
        while ( {
          c.next
        }) {
          if (c.wasPermutated) {
          for (i <- c.getFrom until c.getTo) {

          }}
        else if (c.wasUpdated) {

          }
          else if (c.wasReplaced) {
            update()
          }
          else {




            for (remitem <- 0 until c.getRemoved.size) {
              val lst = KanbanService.getLists(tablePane.b)
              val it = c.getRemoved.get(remitem)
              var ind = coordinateLookUp.find(_._2.listModel == it).map(_._1).getOrElse(0,0)
              val rm = coordinateLookUp(ind)
              tablePane.getChildren.remove(rm)
              def i = ((ind._2 *10 ) + ind._1 + 1) % 10
              def i1 =  ((ind._2 *10 ) + ind._1 + 1) / 10
              var nxt = (i, i1)
              while (ind != ((lst.length)%10, (lst.length)/10)) {
                val toMove = coordinateLookUp(nxt)
                GridPane.setConstraints(toMove , ind._1, ind._2)
                coordinateLookUp.put(ind, toMove)
                ind = nxt
                nxt = (i, i1)
              }
              coordinateLookUp.remove(((lst.length)%10, (lst.length)/10))


            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val itm = c.getAddedSubList.get(additem)
              val ind = tablePane.b.getCardLists.indexOf(itm)
              val coords = coordinateLookUp.keys.toSeq.lastOption.getOrElse((-1,-1))
              val i = (ind) % 10
              val i1 = (ind) / 10
              val l = new ListView(itm, tb)
              GridPane.setConstraints(l, i, i1)
              tablePane.getChildren.add(l)
              coordinateLookUp.put((i, i1), l)

            }
          }
      }
              }
    })
    tablePane.getChildren.clear()
   // Bindings.bindContentBidirectional(listObs.map[Node](new ListView(_)), tablePane.getChildren)
    //for (i <- 0 until 30) addList()










  }



  def update() = {
    val lists = coordinateLookUp
    val lst = KanbanService.getLists(tablePane.b)

    for (i <- lst.indices) {
      val e = lists(i%10 , i/10)
      val c = lst(i)
      if (e.listModel != c) {
        val n = coordinateLookUp.find(_._2.listModel == c) match {case Some(i: ((Int, Int), ListView)) => i}

        GridPane.setConstraints(n._2, i%10, i/10)
        GridPane.setConstraints(e, n._1._1, n._1._2)
        coordinateLookUp.put((i%10, i/10),n._2)
        coordinateLookUp.put(n._1, e)



      }

    }
  }


}

class TableView(board: Models.Board) extends jfxsl.GridPane{
    val b = board
    try {
      var loader = new FXMLLoader(getClass.getResource("/views/TableView.fxml"))

      loader.setRoot(this)
      loader.load()
      var cntrl: TableController = loader.getController
    } catch {
      case e: IOException => throw e
    }

}
