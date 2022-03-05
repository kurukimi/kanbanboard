package Models
import scala.collection.mutable.Buffer


class KanbanModel {
  private val boards = Buffer[Board]()

  def addBoard(name: String) = boards += new Board(name)
  def removeBoard(board: Board) = boards -= board
  def getBoards = boards
}
