package Models
import scala.collection.mutable.Buffer

class CardList(name: String) {
  private val cards = Buffer[Card]()

  def getCards = cards
  def addCard(card: Card) = cards += card
  def removeCard(card: Card) = cards -= card
}
