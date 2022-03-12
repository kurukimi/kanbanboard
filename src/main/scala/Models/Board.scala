package Models
import scala.collection.mutable.Buffer

class Board(name: String) {
  private val cardLists = Buffer[CardList]()

  def addCardList(cardList: CardList) = cardLists += cardList
  def removeCardList(cardList: CardList) = cardLists -= cardList
  def getCardLists = cardLists
}
