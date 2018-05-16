package lectures.collections

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Представим, что по какой-то причине Вам понадобилась своя обертка над списком целых чисел List[Int]
  *
  * Вы приняли решение, что будет достаточно реализовать 4 метода:
  * * * * * def flatMap(f: (Int => MyList)) -  реализуете на основе соответствующего метода из List
  * * * * * метод map(f: (Int) => Int) - с помощью только что полученного метода flatMap класса MyList
  * * * * * filter(???) - через метод flatMap класса MyList
  * * * * * foldLeft(acc: Int)(???) - через декомпозицию на head и tail
  *
  * Для того, чтобы выполнить задание:
  * * * * * раскомментируйте код
  * * * * * замените знаки вопроса на сигнатуры и тела методов
  * * * * * не используйте var и мутабильные коллекции
  *
  */
object MyListImpl extends App {

  class MyList[T, S <: Seq[T]](val data: S) {

    def flatMap[T1, S1 <: Seq[T1]] (f: (T => MyList[T1, S1])): MyList[T1, Seq[T1]] =
      new MyList(data.flatMap(elem => f(elem).data))

    def map[T1](f: (T => T1)) = {
      flatMap{
        elem =>
          new MyList[T1,Seq[T1]](Seq(f(elem)))
      }
    }

    def foldLeft(acc: T)(f: (T, T) => T): T = this.data match {
      case d if d.isEmpty => acc
      case d => new MyList[T, Seq[T]](d.tail).foldLeft(f(acc, d.head))(f)
    }

    def filter(f: T => Boolean): MyList[T, Seq[T]] = {
      def fun: (T => MyList[T,Seq[T]]) = {
        (a: T) => if(f(a)) new MyList(Seq(a)) else new MyList(Seq())
      }
      this.flatMap(fun)
    }
  }

  case class MyListBuffer[T](listBufferData: ListBuffer[T]) extends MyList[T, ListBuffer[T]](listBufferData)

  case class MyIndexedList[T](indexedListData: IndexedSeq[T]) extends MyList[T, IndexedSeq[T]](indexedListData)

  require(new MyList[Int, List[Int]](List(1, 2, 3, 4, 5, 6)).map(p => p * 2).data == List(2, 4, 6, 8, 10, 12))
  require(new MyList[Long, ListBuffer[Long]](ListBuffer(1, 2, 3, 4, 5, 6)).filter(_ % 2 == 0).data == List(2, 4, 6)) // Почему ListBuffer перешел в  List ??
  require(new MyList[Int, List[Int]](List(1, 2, 3, 4, 5, 6)).foldLeft(0)((acc, elem)=> acc + elem) == 21)
  require(new MyList[Float, IndexedSeq[Float]](ArrayBuffer.empty[Float]).foldLeft(0)((acc, elem)=> acc + elem) == 0)

  require(MyIndexedList[Float](ArrayBuffer.empty[Float]).foldLeft(0)((acc, elem) => acc + elem) == 0)
  require(MyListBuffer[Long](ListBuffer(1, 2, 3, 4, 5, 6)).filter(_ % 2 == 0).data == List(2, 4, 6))


}