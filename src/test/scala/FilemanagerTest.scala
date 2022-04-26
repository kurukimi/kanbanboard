import Models.Card
import org.scalatest.funsuite.AnyFunSuite
import utils.FileManager
import utils.FileManager.FileOps

import java.io.IOException

class FilemanagerTest extends AnyFunSuite {
  val path = System.getProperty("user.dir")
  test("loadCard"){
    println(path)
    val file =  FileManager.loadXml(path + "\\src\\test\\scala\\validCard.xml")
    assert(FileManager.xmlToCard(file).text == "Teksti teksti")
    assertThrows[IOException]{
      val invFile = FileManager.loadXml(path + "\\src\\test\\scala\\invalidCard.xml")
    }
  }

  test("saveCard") {
    val crd = new Card("card")
    FileManager.saveXml(crd.toXml, crd.toString)
    val crd2 = FileManager.xmlToCard(FileManager.loadXml(path + s"\\$crd.xml"))
    assert(crd2.text == crd.text)
    assert(crd2.getStartTime.toString == crd.getStartTime.toString)

  }

}
