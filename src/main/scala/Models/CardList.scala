package Models
import scala.collection.mutable.Buffer
import scalafx.collections.ObservableBuffer

class CardList(var name: String) {
  private val cards = ObservableBuffer[Card]()

  def setName(s: String) = name = s
  def getCards = cards.toBuffer
  def addCard(card: Card) = cards += card
  def removeCard(card: Card) = cards.remove(card)
  def getObs = cards
}
