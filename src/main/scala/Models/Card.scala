package Models
import scala.collection.mutable.Buffer

class Card(text: String) {
  private val items = Buffer[CardItem]()
  private val tags = Buffer[String]()

  def getText = text
  def getItems = items
  def addItem(item: CardItem) = items += item
  def removeItem(item: CardItem) = items -= item
  def addTags(tag: String) = tags += tag
  def removeTags(tag: String) = tags -= tag
  def getTags = tags
}
