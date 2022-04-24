package Models
import scala.collection.mutable.Buffer
import com.github.nscala_time.time.Imports._
import scalafx.collections.{ObservableBuffer, ObservableSet}


class Card(var text: String, startTime: DateTime = DateTime.now()) {
  private val items = Buffer[CardItem]()
  private val tags = ObservableSet[String]()

  def setText(s: String) = text = s
  def getText = text
  def getItems = items
  def addItem(item: CardItem) = items += item
  def removeItem(item: CardItem) = items -= item
  def addTags(tag: String) = tags += tag
  def removeTags(tag: String) = tags -= tag
  def getTags = tags.toSet
  def getObsTags = tags
  def getStartTime = startTime
  def getTimeElapsed = (startTime to DateTime.now()).millis / (1000 * 60 * 60)



}
