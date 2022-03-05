package Models
import scala.collection.mutable.Buffer

class Board(name: String) {
  private val cardLists = Buffer[CardList]()

  def addCardList(name: String) = cardLists += new CardList(name)
  def removeCardList(cardList: CardList) = cardLists -= cardList
  def getCardLists = cardLists
}
