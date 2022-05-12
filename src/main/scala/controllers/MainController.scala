package controllers

import Models.{Board, Card, CardList, KanbanModel}
import Service.KanbanService
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Alert, MenuButton, MenuItem, ScrollPane, Tab, TabPane, TextField, TextInputDialog}
import javafx.scene.{Node, control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.Includes._
import javafx.scene.layout.{AnchorPane, GridPane, Pane, Priority}
import javafx.collections.ListChangeListener
import javafx.scene.control.Alert.AlertType
import scala.jdk.CollectionConverters._
import javafx.stage.FileChooser
import java.net.URL
import java.util



class MainController extends jfxf.Initializable{
  @jfxf.FXML
  var TableVController: TableController = _
  // initial tab containing table
  @jfxf.FXML
  var tab1: Tab = _
  @jfxf.FXML
  var archive: MenuButton = _
  @jfxf.FXML
  var tabPane: TabPane = _
  @jfxf.FXML
  var filter: TextField = _


  @jfxf.FXML
  private def handleListAdd(event: jfxe.ActionEvent): Unit = {
    tabPane.getSelectionModel.getSelectedItem.getContent match {
      case tabContent: ScrollPane => {
            tabContent.getContent match {
              case scrlPaneContent: TableView => {
                KanbanService.addList("Title", scrlPaneContent.board)
              }
            }
      }
      case _ =>
    }
  }

  @jfxf.FXML
  def loadCard() = {
    // Get board of selected tab
    val board = tabPane.getSelectionModel.getSelectedItem.getContent match {
      case tabContent: ScrollPane => tabContent.getContent match {
        case scrlPaneContent: TableView => scrlPaneContent.board
      }

    }
    // if board has lists: allow card load
    // else show alert
    if (KanbanService.getLists(board).nonEmpty) {
      val fileChooser = new FileChooser()
      fileChooser.getExtensionFilters.add(new FileChooser.ExtensionFilter("XML Files", "*.xml"))
      val file = fileChooser.showOpenDialog(tabPane.getScene.getWindow)
      if (file != null) {
        val maybeCard = KanbanService.loadCard(file.getPath)
        maybeCard match {
          case Some(c: Card) => KanbanService.getLists(board).head.addCard(c)
          case _ => {val alert = new Alert(AlertType.ERROR); alert.setContentText("File load failed"); alert.show()}
        }
      }
    } else {
      val alert = new Alert(AlertType.INFORMATION)
      alert.setContentText("Add list to load a card")
      alert.show()
    }
  }

  @jfxf.FXML
  private def addTable(event: jfxe.ActionEvent) = {
    KanbanService.addBoard(s"table ${KanbanService.getBoards.length+1}")
  }

  @jfxf.FXML
  override def initialize(url: URL, rb: util.ResourceBundle): Unit = {

    def boardToScrollPane(board: Board) = {
      val scrollPane = new ScrollPane()
      scrollPane.hgrow = Priority.ALWAYS
      scrollPane.setFitToWidth(true)
      scrollPane.setFitToWidth(true)
      scrollPane.setContent(new TableView(board))
      scrollPane
    }


    def cardToArchiveMenuItem(card: Card) = {
      val menuItem = new MenuItem()
      menuItem.setId(card.toString)
      menuItem.setText(card.text)
      // action when clicked
      menuItem.setOnAction((e) => {
        // if exists board with list(s): remove this item from archive
        if (KanbanService.getBoards.exists(_.getCardLists.nonEmpty)) {
          val board = tabPane.getSelectionModel.getSelectedItem.getContent match {
            case tabContent: ScrollPane => tabContent.getContent match {
              case scrlPaneContent: TableView => scrlPaneContent.board
            }
          }
          KanbanService.removeFromArchive(card, KanbanService.getLists(board).head)
        }
      })
      menuItem
    }

    tabPane.getTabs.addListener(new ListChangeListener[Tab]() {
      override def onChanged(change: ListChangeListener.Change[_ <: Tab]): Unit = {
        // make it so first tab can't be closed
        tabPane.getTabs.foreach(_.setClosable(true))
        tabPane.getTabs.get(0).setClosable(false)
      }
    })

    filter.textProperty.addListener(new ChangeListener[String]() {
      override def changed(observableValue: ObservableValue[_ <: String], oldStr: String, newStr: String): Unit = {
        val maybeCardviews = tabPane.getSelectionModel.getSelectedItem.getContent.lookupAll("#cardV")
        if (maybeCardviews != null) {
          val maybeCardViewsSet: Set[Node] = maybeCardviews.asScala.toSet
          if (newStr.nonEmpty) {
            val filteredCards = KanbanService.filter(newStr)
            // hide cards not returned by the filter
            maybeCardViewsSet.foreach {
                case cView: CardView => if (!filteredCards.contains(cView.card)) cView.setVisible(false) else cView.setVisible(true)
              }
          } else {
            maybeCardViewsSet.foreach {
              case x: CardView => x.setVisible(true)
            }
          }
          }}})


    KanbanService.addBoard("table 1")
    tab1.setContent(boardToScrollPane(KanbanService.getBoards.head))
    tab1.setClosable(false)
    tab1.setOnCloseRequest((e) => {
      tab1.getContent match {
      case tabContent: ScrollPane => tabContent.getContent match {
        case scrlPaneContent: TableView => KanbanService.removeBoard(scrlPaneContent.board)
        case _ =>
      }
      case _ =>
    }
    })


    KanbanService.getBoardObs.delegate.addListener(new ListChangeListener[Board]() {
      override def onChanged(c: ListChangeListener.Change[_ <: Board]): Unit = {
        while ( {
          c.next
        }) if (c.wasPermutated) for (i <- c.getFrom until c.getTo) {
          }
        else if (c.wasUpdated) {
          }
          else {

            for (remitem <- 0 until c.getRemoved.size) {
              val board = c.getRemoved.get(remitem)
              val maybeTab = tabPane.getTabs.find(t => t.getContent match {
                case scrl: ScrollPane => scrl.getContent match {
                  case tableView: TableView => tableView.board == board
                }
              })
              tabPane.getTabs.remove(maybeTab)
            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val board = c.getAddedSubList.get(additem)
              val tab = new Tab()
              tab.setOnClosed((e) => {
                KanbanService.removeBoard(tab.getContent match {
                  case scrlView: ScrollPane => scrlView.getContent match {
                    case tableView: TableView => tableView.board
                  }
                })})

              tab.setText(board.name)
              tab.setContent(boardToScrollPane(board))
              tabPane.getTabs.add(tab)
            }
          }
      }
    })


    KanbanService.getArchive.getObs.addListener(new ListChangeListener[Card]() {
      override def onChanged(c: ListChangeListener.Change[_ <: Card]): Unit = {
        while ( {
          c.next
        }) if (c.wasPermutated) for (i <- c.getFrom until c.getTo) {
          }
        else if (c.wasUpdated) {
          }
          else {
            for (remitem <- 0 until c.getRemoved.size) {
              val card = c.getRemoved.get(remitem)
              val archivedCard = archive.getItems.find(x => (x.getId == card.toString)).getOrElse(None)
              archive.getItems.remove(archivedCard)
            }
            for (additem <- 0 until c.getAddedSubList.size) {
              val card = c.getAddedSubList.get(additem)
              archive.getItems.add(cardToArchiveMenuItem(card))
            }
          }
      }
      })
    }

}


