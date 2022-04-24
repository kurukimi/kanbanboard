package Models
import scala.collection.mutable.Buffer
import scalafx.collections.ObservableBuffer

class KanbanModel {
  private val boards = ObservableBuffer[Board]()
  private val archive = Archive

  def getArchive = archive
  def addBoard(board: Board) = boards += board
  def removeBoard(board: Board) = boards.remove(board)
  def getBoards = boards.toBuffer
  def getObs = boards
}
