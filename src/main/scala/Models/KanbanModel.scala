package Models
import scala.collection.mutable.Buffer


class KanbanModel {
  private val boards = Buffer[Board]()
  private val archive = Archive

  def getArchive = archive
  def addBoard(board: Board) = boards += board
  def removeBoard(board: Board) = boards -= board
  def getBoards = boards
}
