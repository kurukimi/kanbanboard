package Models
import scala.collection.mutable.Buffer
import scalafx.collections.ObservableBuffer

class Board(val name: String) {
  private val cardLists = ObservableBuffer[CardList]()


  def addCardList(cardList: CardList) = cardLists += cardList
  def removeCardList(cardList: CardList) = cardLists.remove(cardList)
  def getCardLists = cardLists.toBuffer
  def getObs = cardLists
}
