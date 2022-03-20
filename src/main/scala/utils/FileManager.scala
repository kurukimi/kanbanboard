package utils
import Models.Card
import utils.FileManager.FileOps
import scala.xml.Elem

trait FileManager[A] {
  def toXml(a: A): Unit

}


object FileManager {
  def apply[A](implicit sh: FileManager[A]): FileManager[A] = sh

  implicit class FileOps[A: FileManager](a: A) {
    def toXml = FileManager[A].toXml(a)
  }

  def loadXml(path: String) = scala.xml.XML.loadFile(path)

  implicit val cardConv: FileManager[Card] = {
    card => {
      scala.xml.XML.save(s"$card.xml",
      <name>{card.getText}</name>
      )
    }
  }


}

object maint extends App{
  val kortti = new Card("testi kortti")
  kortti.toXml
  println(FileManager.loadXml(s"$kortti.xml"))
}