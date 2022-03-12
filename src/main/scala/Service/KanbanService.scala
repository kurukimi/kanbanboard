package Service
import Models._

object KanbanService {
  private val kanban = new KanbanModel


  def addBoard(name: String) = kanban.addBoard(new Board(name))
  def removeBoard(board: Board) = kanban.removeBoard(board)
  def getBoards = kanban.getBoards

  def addList(name: String)(board: Board) = board.addCardList(new CardList(name))
  def removeList(list: CardList)(board: Board) = board.removeCardList(list)
  def getLists(board: Board) = board.getCardLists

  def addCard(content: String, cList: CardList) = cList.addCard(new Card(content))
  def removeCard(card: Card, cList: CardList) = cList.removeCard(card)
  def getCards(cList: CardList) = cList.getCards

  def archiveCard(card: Card, cardList: CardList) = {
    kanban.getArchive.addCard(card)
    this.removeCard(card, cardList)
  }
  def removeFromArchive(card: Card, to: CardList) = {
    to.addCard(card)
    kanban.getArchive.removeCard(card)
  }
  def getArchive = kanban.getArchive

  def filter(q: String) = {
    kanban.getBoards.flatMap(
      b => b.getCardLists.flatMap(
      c => c.getCards.filter(
      _.getTags.contains(q))))
  }

}
