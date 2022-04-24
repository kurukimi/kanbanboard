package Service
import Models._
import utils.FileManager._

import java.io.IOException


object KanbanService {
  val kanban = new KanbanModel

  def addBoard(name: String) = kanban.addBoard(new Board(name))
  def removeBoard(board: Board) = kanban.removeBoard(board)
  def getBoards = kanban.getBoards
  def getBoardObs = kanban.getObs

  def addList(name: String, board: Board) = {
    val l = new CardList(name)
    board.addCardList(l)
    l
  }
  def removeList(list: CardList)(board: Board) = board.removeCardList(list)
  def getLists(board: Board) = board.getCardLists
  def getListObs(board: Board) = board.getObs

  def addCard(content: String, cList: CardList) = {
    val c = new Card(content)
    cList.addCard(c)
    c
  }
  def removeCard(card: Card, cList: CardList) = cList.removeCard(card)
  def getCards(cList: CardList) = cList.getCards

  def archiveCard(card: Card, cardList: CardList) = {
    kanban.getArchive.addCard(card)
    cardList.removeCard(card)
  }
  def removeFromArchive(card: Card, to: CardList) = {
    to.addCard(card)
    kanban.getArchive.removeCard(card)
  }
  def getArchive = kanban.getArchive

  def saveCard(card: Card, path: String) = {
    try {
      saveXml(card.toXml, path + s"\\{$card}")
      true
    } catch {
      case e: IOException => false
    }

    }

  def loadCard(path: String) = {
    try {
      Some(xmlToCard(loadXml(path)))
    } catch {
      case e: IOException => None
    }
  }

  def filter(q: String) = {
    kanban.getBoards.flatMap(
      b => b.getCardLists.flatMap(
      c => c.getCards.filter(
      _.getTags.exists(_.toLowerCase.contains(q.toLowerCase)) )))
  }

}
