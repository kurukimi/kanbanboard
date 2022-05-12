package utils
import Models.Card
import Models.CheckList
import Models.TextItem
import Models.CardItem
import org.joda.time.DateTime
import java.io.IOException
import scala.xml.Elem

trait FileManager[A] {
  def toXml(a: A): Elem
}


object FileManager{
  def apply[A](implicit sh: FileManager[A]): FileManager[A] = sh

  implicit class FileOps[A: FileManager](a: A) {
    def toXml = FileManager[A].toXml(a)
  }

  @throws(classOf[IOException])
  def loadXml(path: String)  = {
      val e = scala.xml.XML.loadFile(path)
      if ((e \\ "text").isEmpty | (e \\ "time").text.isEmpty) throw new IOException
      e
    }
  @throws(classOf[IOException])
  def saveXml(e: Elem, name: String) = {
      scala.xml.XML.save( s"${name}.xml", e)
  }

  def xmlToCard(el: Elem) = {
    val text = (el \\ "text").text
    val startTime = (el \\ "time").text
    val card = new Card(text, startTime = DateTime.parse(startTime))
    (el \\ "textItem").foreach(x => card.addItem(new TextItem(x.text)))
    (el \\ "tag").foreach(x => card.addTags(x.text))
    card
  }

  implicit val cardConv: FileManager[Card] = {
    card => {
      <card id={s"$card"}>
        <text>{card.getText}</text>
        <time>{card.getStartTime}</time>
        <tags>
          {card.getTags.map(x => <tag>{x}</tag>)}
        </tags>
        <cardItems>
          {card.getItems.map(x => x.toXml)}
        </cardItems>
      </card>

    }
  }

  implicit val cardItemConv: FileManager[CardItem] = {
    item => {
        item match {
        case list: CheckList => {
          <checkList>
             <items>
              {list.getItems.map(x =>
              <done>{x.done}</done>
               <job>{x.job}</job>
              )}
            </items>
            <progress>{list.progress}</progress>
          </checkList>
        }
        case item: TextItem => <textItem>{item.getContent}</textItem>
        case _ => <item></item>
      }

  }
  }
}





