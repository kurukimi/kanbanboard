package Models
import scala.collection.mutable.Buffer

class CardList(name: String) {
  private val cards = Buffer[Card]()

  def getCards = cards
  def addCard(text: String) = cards += new Card(text)
  def removeCard(card: Card) = cards -= card
}
