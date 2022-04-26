import Service.KanbanService
import Models._
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import collection.mutable.Buffer

class ServiceTest extends AnyFunSuite with BeforeAndAfter {

  var listBoard = new Board("listBoard")
  var cList = new CardList("testList")


  test("KanbanService.addBoard") {
    for (i <- 1 to 10) {
      KanbanService.addBoard("testboard" + i)
    }

    assert(KanbanService.kanban.getBoards.head == KanbanService.getBoards.head)
    assert(KanbanService.kanban.getBoards.length == 10)
    assert(KanbanService.kanban.getBoards.head.name == "testboard1")
    assert(KanbanService.kanban.getBoards.last.name == "testboard10")

  }



  test("KanbanService.removeBoard") {
    val brd = new Board("testboard")

    KanbanService.kanban.addBoard(brd)
    assert(KanbanService.kanban.getBoards.contains(brd))
    KanbanService.removeBoard(brd)
    assert(!KanbanService.kanban.getBoards.contains(brd))
  }


  test("KanbanServie.getBoards") {
    KanbanService.getBoards.indices.foreach(i => assert(KanbanService.getBoards(i).name == s"testboard${i+1}"))
    KanbanService.getBoards.indices.foreach(i => assert(KanbanService.kanban.getBoards(i) == KanbanService.getBoards(i)))
  }


  test("KanbanService.addList") {
  for (i <- 1 to 10) {
    KanbanService.addList("testList" + i, listBoard)
  }
  assert(listBoard.getCardLists.head == KanbanService.getLists(listBoard).head)
  assert(listBoard.getCardLists.length == 10)
  assert(listBoard.getCardLists.head.name == "testList1")
  assert(listBoard.getCardLists.last.name == "testList10")
}

  test("KanbanService.removeList") {
    val lst = new CardList("testlist")

    listBoard.addCardList(lst)
    assert(listBoard.getCardLists.contains(lst))
    KanbanService.removeList(lst)(listBoard)
    assert(!listBoard.getCardLists.contains(lst))
  }

  test("KanbanServie.getLists") {
    listBoard.getCardLists.indices.foreach(i => assert(KanbanService.getLists(listBoard)(i).name == s"testList${i+1}"))
    listBoard.getCardLists.indices.foreach(i => assert(listBoard.getCardLists(i) == KanbanService.getLists(listBoard)(i)))
  }



  test("KanbanService.addCard") {
  for (i <- 1 to 10) {
    KanbanService.addCard("testCard" + i, cList)
  }
  assert(cList.getCards.head == KanbanService.getCards(cList).head)
  assert(cList.getCards.length == 10)
  assert(cList.getCards.head.text == "testCard1")
  assert(cList.getCards.last.text == "testCard10")
}

  test("KanbanService.remooveCard") {
    val crd = new Card("testCard")

    cList.addCard(crd)
    assert(cList.getCards.contains(crd))
    KanbanService.removeCard(crd, cList)
    assert(!cList.getCards.contains(crd))
  }

  test("KanbanService.getCards") {
    cList.getCards.indices.foreach(i => assert(KanbanService.getCards(cList)(i).text == s"testCard${i+1}"))
    cList.getCards.indices.foreach(i => assert(cList.getCards(i) == KanbanService.getCards(cList)(i)))
  }

  test("KanbanService.ArchiveCard") {
    val lst = KanbanService.getLists(listBoard).head
    val crd = new Card("testc")
    lst.addCard(crd)
    KanbanService.archiveCard(crd, lst)
    assert(!KanbanService.getLists(listBoard).head.getCards.contains(crd))
    assert(KanbanService.kanban.getArchive.getCards.contains(crd))
  }
  test("KanbanService.removeFromArhive") {
    val lst = KanbanService.getLists(listBoard).head
    val crd = new Card("testcard")
    lst.addCard(crd)
    KanbanService.kanban.getArchive.addCard(crd)
    KanbanService.removeFromArchive(crd, lst)
    assert(!KanbanService.kanban.getArchive.getCards.contains(crd))
    assert(KanbanService.getLists(listBoard).head.getCards.contains(crd))
  }
  test("KanbanService.saveCard") {
    val crd = new Card("saveCard")
    val s = KanbanService.saveCard(crd, System.getProperty("user.dir"))
    assert(s)
    val f = KanbanService.saveCard(crd, "")
    assert(!f)
  }
  test("KanbanService.loadCard") {
    val crd = new Card("saveCard")
    KanbanService.saveCard(crd, System.getProperty("user.dir"))

    val l = KanbanService.loadCard(System.getProperty("user.dir") + s"\\${crd}.xml")
    val unw = l match {
      case Some(z: Card) =>z
    }
    assert(unw.text == crd.text)
    assert(unw.getTags == crd.getTags)
    assert(unw.getTimeElapsed == crd.getTimeElapsed)
  }



  test("KanbanService.filter") {
    val lst = new CardList("flt")
    val res = KanbanService.filter("1")
    assert(res.isEmpty)

    val crd1 = new Card("test")
    val crd2 = new Card("test2")
    crd1.addTags("testi1")
    crd2.addTags("testi2")
    lst.addCard(crd1)
    lst.addCard(crd2)
    KanbanService.addBoard("brd")
    KanbanService.kanban.getBoards.head.addCardList(lst)
    val res2 = KanbanService.filter("test")

    assert(res2.head.getTags.contains("testi1"))
    assert(res2.last.getTags.contains("testi2"))
    val res3 = KanbanService.filter("1")
    assert(res3.head.getTags.contains("testi1"))
  }


}
