package controllers
import Models.CardList
import Service.KanbanService
import javafx.collections.ListChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.layout.GridPane
import javafx.scene.{layout => jfxsl}
import javafx.{fxml => jfxf}

import java.io.IOException
import java.net.URL
import java.util
import scala.collection.mutable.Map



class TableController extends jfxf.Initializable{
  val colCount = 10
  val coordinateLookUp = Map[(Int, Int) , ListView]()
  val tb = this


  @jfxf.FXML
  var tablePane: TableView = _
  @jfxf.FXML
  var ListVController: ListController = _

  def delList(l: ListView): Unit = {
    KanbanService.removeList(l.listModel)(tablePane.board)
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    val lists = KanbanService.getListObs(tablePane.board)
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
              val lists = KanbanService.getLists(tablePane.board)
              val cardList = c.getRemoved.get(remitem)
              // get coordinates of removed cardList, so we can start collapsing from there
              var coords = coordinateLookUp.find(_._2.listModel == cardList).map(_._1).getOrElse(0,0)
              val listView = coordinateLookUp(coords)
              tablePane.getChildren.remove(listView)
              // simple arithmetics calculating next coordinates
              def newCoordX = ((coords._2 * colCount ) + coords._1 + 1) % colCount
              def newCoordY =  ((coords._2 * colCount ) + coords._1 + 1) / colCount
              var newCoords = (newCoordX, newCoordY)
              // move the lists that were after the removed list one coordinate back
              while (coords != ((lists.length) % colCount, (lists.length) / colCount )) {
                val toMove = coordinateLookUp(newCoords)
                GridPane.setConstraints(toMove , coords._1, coords._2)
                coordinateLookUp.put(coords, toMove)
                coords = newCoords
                newCoords = (newCoordX, newCoordY)
              }
              // need to remove last coordinate list
              coordinateLookUp.remove(((lists.length) % colCount, (lists.length) / colCount))
            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val cardList = c.getAddedSubList.get(additem)
              val ind = tablePane.board.getCardLists.indexOf(cardList)
              val coords = coordinateLookUp.keys.toSeq.lastOption.getOrElse((-1,-1))
              val newX = (ind) % colCount
              val newY = (ind) / colCount
              val listView = new ListView(cardList, tb)
              GridPane.setConstraints(listView, newX, newY)
              tablePane.getChildren.add(listView)
              coordinateLookUp.put((newX, newY), listView)
            }
          }
      }
      }
    })
    tablePane.getChildren.clear()

  }



  def update() = {
    val cardLists = KanbanService.getLists(tablePane.board)
    for (index <- cardLists.indices) {
      val (i1, i2) = (index % colCount, index / colCount)
      val listView = coordinateLookUp(i1 , i2)
      val cardList = cardLists(index)
      if (listView.listModel != cardList) {
        val (coords, lView) = coordinateLookUp.find(_._2.listModel == cardList) match {case Some(i: ((Int, Int), ListView)) => i}
        GridPane.setConstraints(lView, i1, i2)
        GridPane.setConstraints(listView, coords._1, coords._2)
        coordinateLookUp.put((i1, i2),lView)
        coordinateLookUp.put(coords, listView)
      }
    }
  }
}




class TableView(val board: Models.Board) extends jfxsl.GridPane{

    try {
      var loader = new FXMLLoader(getClass.getResource("/views/TableView.fxml"))
      loader.setRoot(this)
      loader.load()
      var cntrl: TableController = loader.getController
    } catch {
      case e: IOException => throw e
    }

}
