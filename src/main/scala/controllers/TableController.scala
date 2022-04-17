package controllers

import Service.KanbanService
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.{ColumnConstraints, Priority, RowConstraints}
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane}
import scalafx.scene.layout.GridPane

import java.net.URL
import java.util


class TableController extends jfxf.Initializable{
  KanbanService.addBoard("nimi")
  val board: Models.Board = KanbanService.getBoards(0)
  val listviews = KanbanService.getLists(this.board)
  var counter = 0
  @jfxf.FXML
  var tablePane: jfxsl.GridPane = _
  @jfxf.FXML
  var ListController: ListController = _


  println(this)

  @jfxf.FXML
  def addList() = {
    var inp = new TextInputDialog()
    var res = inp.showAndWait()
    res.ifPresent( x => {
    val l = KanbanService.addList(x, this.board)
    val row = new RowConstraints(100)
    if (counter % 10 == 0)  tablePane.getRowConstraints.add(row)
    tablePane.add(new ListView(l), counter % 10, counter / 10)
    counter += 1
    })
  }

  def apply() = {

    /*var counter = 0
    val lists = KanbanService.getLists(this.board)
    tablePane.getChildren.
    lists.foreach(x => {
      val row = new RowConstraints(100)
      if (counter % 10 == 0)  tablePane.getRowConstraints.add(row)
      tablePane.add(new ListView(x), counter % 10, counter / 10); counter += 1

    }) **/
  }

  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {
    val lists = KanbanService.getLists(this.board)



  }

}
