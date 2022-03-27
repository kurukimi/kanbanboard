package Models
import scala.collection.mutable.Buffer

class CheckList extends CardItem {
  case class checkListItem(job: String, done: Boolean = false)
  val items = Buffer[checkListItem]()

  def addItem(name: String) = items += checkListItem(name)
  def getItems = items
  def progress = items.count(x => x.done) / items.length


}
