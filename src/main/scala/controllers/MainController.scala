package controllers

import Models.{Board, Card, CardList, KanbanModel}
import Service.KanbanService
import javafx.beans.binding.Bindings
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Alert, MenuButton, MenuItem, ScrollPane, Tab, TabPane, TextField, TextInputDialog}
import javafx.scene.{Node, control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import scalafx.scene.control.Button
import javafx.scene.layout.{AnchorPane, GridPane, Pane, Priority}
import javafx.collections.ListChangeListener
import javafx.scene.control.Alert.AlertType

import scala.jdk.CollectionConverters._
import javafx.stage.FileChooser

import java.net.URL
import java.util
import scala.collection.IterableOnce.iterableOnceExtensionMethods


class MainController extends jfxf.Initializable{

  @jfxf.FXML
  var TableVController: TableController = _
  @jfxf.FXML
  var tab1: Tab = _
  @jfxf.FXML
  var archive: MenuButton = _
  @jfxf.FXML
  var tabP: TabPane = _
  @jfxf.FXML
  var filter: TextField = _


  @jfxf.FXML
  private def handleListAdd(event: jfxe.ActionEvent): Unit = {
    tabP.getSelectionModel.getSelectedItem.getContent match {
      case x: ScrollPane => {
            x.getContent match {
              case i: TableView => {

                KanbanService.addList("Title", i.b)

              }
            }

      }
      case _ =>
    }

  }

  @jfxf.FXML
  def loadCard() = {
    val brd = tabP.getSelectionModel.getSelectedItem.getContent match {
      case x: ScrollPane => x.getContent match {
        case z: TableView => z.b
      }
    }
    if (KanbanService.getLists(brd).nonEmpty) {
      val fl = new FileChooser()
      fl.getExtensionFilters.add(new FileChooser.ExtensionFilter("XML Files", "*.xml"))
      val sel = fl.showOpenDialog(tabP.getScene.getWindow)

      if (sel != null) {
        val crd = KanbanService.loadCard(sel.getPath)
        crd match {
          case Some(c: Card) => KanbanService.getLists(brd).head.addCard(c)
          case _ => {val al = new Alert(AlertType.ERROR); al.setContentText("File load failed"); al.show()}
        }

      }

    } else {
      val a = new Alert(AlertType.INFORMATION)
      a.setContentText("Add list to load a card")
      a.show()
    }

  }

  @jfxf.FXML
  private def addTable(event: jfxe.ActionEvent) = {

    KanbanService.addBoard(s"table ${KanbanService.getBoards.length+1}")
  }

  @jfxf.FXML
  private def handleArchive(event: jfxe.ActionEvent) = {
    TableVController.ListVController
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {

    println("mainc  called")
    def scrl(itm: Board) = {
      val scr = new ScrollPane()
      scr.hgrow = Priority.ALWAYS
      scr.setFitToWidth(true)
      scr.setFitToWidth(true)
      scr.setContent(new TableView(itm))
      scr
    }
    def menui(itm: Card) = {
      val mn = new MenuItem()
      mn.setId(itm.toString)
      mn.setText(itm.text)
      mn.setOnAction((e) => {
        if (KanbanService.getBoards.nonEmpty && KanbanService.getBoards.exists(_.getCardLists.nonEmpty)) {

          val brd = tabP.getSelectionModel.getSelectedItem.getContent match {
            case x: ScrollPane => x.getContent match {
              case z: TableView => z.b
            }
          }
          KanbanService.removeFromArchive(itm, KanbanService.getLists(brd).head)
        }
      })
      mn
    }

    tabP.getTabs.addListener(new ListChangeListener[Tab]() {
      override def onChanged(change: ListChangeListener.Change[_ <: Tab]): Unit = {
        tabP.getTabs.foreach(_.setClosable(true))
        tabP.getTabs.get(0).setClosable(false)
      }
    })

    filter.textProperty.addListener(new ChangeListener[String]() {
      override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit = {
        if (t1.nonEmpty){
          val lkp = tabP.getSelectionModel.getSelectedItem.getContent.lookupAll("#cardV")
          if (lkp != null) {
            val filtr = KanbanService.filter(t1)
              val cl: Set[Node] = lkp.asScala.toSet
              cl.foreach {
                case x: CardView => if (!filtr.contains(x.card)) x.setVisible(false) else x.setVisible(true)
              }
          }
        } else {
          val lkp = tabP.getSelectionModel.getSelectedItem.getContent.lookupAll("#cardV")
          if (lkp != null) {
              val cl: Set[Node] = lkp.asScala.toSet
              cl.foreach {
                case x: CardView => x.setVisible(true)
              }
          }

        }
      }})


    KanbanService.addBoard("table 1")
    tab1.setContent(scrl(KanbanService.getBoards(0)))
    tab1.setClosable(false)
    tab1.setOnCloseRequest((e) => {tab1.getContent match {
      case t: ScrollPane => t.getContent match {
        case x: TableView => KanbanService.removeBoard(x.b)
        case _ =>
      }
      case _ =>
    }

    })

    val obs = KanbanService.getBoardObs.delegate
    obs.addListener(new ListChangeListener[Board]() {
      override def onChanged(c: ListChangeListener.Change[_ <: Board]): Unit = {
        while ( {
          c.next
        }) if (c.wasPermutated) for (i <- c.getFrom until c.getTo) {

          }
        else if (c.wasUpdated) {

          }
          else {

            for (remitem <- 0 until c.getRemoved.size) {
              val it = c.getRemoved.get(remitem)
              val rem = tabP.getTabs.find(t => t.getContent match {
                case c: ScrollPane => c.getContent match {
                  case z: TableView => z.b == it
                }
              })
              tabP.getTabs.remove(rem)


            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val itm = c.getAddedSubList.get(additem)
              val t = new Tab()
              t.setOnClosed((e) => {KanbanService.removeBoard(t.getContent match {
                case i: ScrollPane => i.getContent match {
                  case v: TableView => v.b
                }
              })})
              t.setText(itm.name)
              t.setContent(scrl(itm))
              tabP.getTabs.add(t)


            }
          }
      }
    })



    val arc = KanbanService.getArchive.getObs
    arc.addListener(new ListChangeListener[Card]() {
      override def onChanged(c: ListChangeListener.Change[_ <: Card]): Unit = {
        while ( {
          c.next
        }) if (c.wasPermutated) for (i <- c.getFrom until c.getTo) {

          }
        else if (c.wasUpdated) {

          }
          else {
            for (remitem <- 0 until c.getRemoved.size) {
              val itm = c.getRemoved.get(remitem)
              val del = archive.getItems.find(x => (x.getId == itm.toString)).getOrElse(None)
              archive.getItems.remove(del)

            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val itm = c.getAddedSubList.get(additem)

              archive.getItems.add(menui(itm))


            }
          }
      }
      })
    }







}


