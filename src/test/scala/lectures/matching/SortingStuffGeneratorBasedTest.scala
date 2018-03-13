package lectures.matching

import lectures.matching.SortingStuff._
import org.scalacheck.Gen
import org.scalactic.anyvals.PosInt
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}


import scala.util.Random


/**
  * Короткий список самых востребованных генераторов:
  * Gen.alphaString
  * Gen.delay
  * Gen.oneOf
  * Gen.resultOf
  * Gen.zip
  * Gen.map
  * Gen.suchThat
  * Gen.mapOf
  * Gen.pic
  * Gen.choose
  *
  * Допишите 2 теста:
  * Для "find knife" теста создайте генератор, Option[Knife]. Тест должен показать, что если нож есть в вещах,
  * то метод findMyKnife его отыщет.
  *
  * Для "put boots ..." создайте генератор и проверьте правильность работы метода sortJunk по аналогии с предыдущими тестами.
  *
  */

class SortingStuffGeneratorBasedTest extends WordSpec with Matchers with PropertyChecks {

  val cheepWatchGen: Gen[Watches] = Gen.zip(Gen.choose(0f, 1000f), Gen.alphaStr).map(w => Watches(w._2, w._1))
  val bookGenerator: Gen[Book] = Gen.alphaStr.map(name => Book(name, Random.nextBoolean()))
  val interestingBookGen: Gen[Book] = bookGenerator.filter(_.isInteresting)
  val knifeGenerator = Gen.option(Knife)
  val bootsGenerator:Gen[Boots] = Gen.zip(Gen.alphaStr, Gen.choose(0, 1000)).map(b => Boots(b._1, b._2))
//  val stuffGenerator = Gen.sized { size =>
//    for {
//      watchesList <- Gen.listOfN(size / 2, cheepWatchGen)
//      booksList <- Gen.listOfN(size / 2, bookGenerator)
//      knife <- knifeGenerator
//      book <- bookGenerator
//    } yield (knife, watchesList ++ booksList ++ List(knife.getOrElse(book)))
//  }
//  val stuffBoxGenerator = for {
//    (knifeFlag, stuff) <- stuffGenerator
//  } yield (knifeFlag, StuffBox(Nil, Nil, Nil, stuff))

  // Override configuration if you need
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSize = 10)

  val get: AfterWord = new AfterWord("have")

  "This test" should get {
    "proper cheep watch generator" in {
      forAll(cheepWatchGen) { (watch: Watches) => {
        watch.cost should be <= 1000f
      }
      }
    }
    "proper interesting book generator" in {
      val books = interestingBookGen
      forAll(books) { (book: Book) => {
        book shouldBe 'interesting
      }
      }
    }
  }

  "Sort stuff" should {
    "return collections" which {
      "total size is equal to item amount" in pendingUntilFixed{
        val ms = generatorDrivenConfig.minSuccessful

        val books = (1 to ms) flatMap { _ => interestingBookGen.sample }
        val watches = (1 to ms) flatMap { _ => cheepWatchGen.sample }

        val StuffBox(goodBooks, niceWatches, _, junk) = SortingStuff.sortJunk(Random.shuffle(books ++ watches).toList)
        goodBooks should have size books.size
        niceWatches should have size 0
        junk should have size watches.size
      }
    }


    "find knife" which {
      "was occasionally disposed" in {
        val ms = generatorDrivenConfig.minSuccessful
        val books = (1 to ms) flatMap { _ => interestingBookGen.sample }
        val watches = (1 to ms) flatMap { _ => cheepWatchGen.sample }
        var knifeOpt:Option[Knife.type ] = None
        for (knife <- knifeGenerator) yield {
          knifeOpt = knife
        }

        val knifeOrBook = knifeOpt.getOrElse(books.head)

        val st = StuffBox(Nil, Nil, Nil, (books ++ watches ++ List(knifeOrBook)).toList)

        SortingStuff.findMyKnife(st) shouldBe knifeOpt.nonEmpty
      }
    }

    "put boots in a proper place" when {
      "boots were produced by Converse or Adidas" in {
        val ms = generatorDrivenConfig.minSuccessful
        val books = (1 to ms) flatMap { _ => interestingBookGen.sample }
        val watches = (1 to ms) flatMap { _ => cheepWatchGen.sample }
        val boots = (1 to ms) flatMap { _ => bootsGenerator.sample }
        val StuffBox(goodBooks, niceWatches, strangeBoots, junk) = SortingStuff.sortJunk(Random.shuffle(books ++ watches ++ boots).toList)
        goodBooks should have size books.size
        niceWatches should have size 0
        junk should have size watches.size + boots.size
        strangeBoots should have size 0

      }
    }
  }
}
