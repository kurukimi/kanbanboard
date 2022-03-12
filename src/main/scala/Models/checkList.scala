package Models
import scala.collection.mutable.Buffer

class checkList extends CardItem {
  case class checkListItem(job: String, done: Boolean = false)
  val items = Buffer[checkListItem]()

  def addItem(name: String) = items += checkListItem(name)

}
