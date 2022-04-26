package Models

class TextItem(private var content: String) extends CardItem {
  def getContent = content
  def setContent(newC: String) = content = newC
}
