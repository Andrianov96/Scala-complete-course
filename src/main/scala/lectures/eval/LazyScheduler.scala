package lectures.eval

import java.time.{Clock, Instant}

import scala.collection.IterableView.Coll
import scala.collection.SeqView

/**
  * В этом задании ваша задча реализовать своеобразный View с таймером.
  *
  * Он должен представлять из себя стандартный SeqView c ограничением по времени.
  * Т.е. этот view ведет себя как обычно, пока не истечет таймаут, предеданный при создании.
  * Как только таймаут истекает, view должен начать вести себя так, как будто коллекция пуста.
  *
  * Для решения задачи подставьте на место вопросительных знаков реализацию view.
  * Раскомментируйте и выполните тесты в lectures.eval.LazySchedulerTest
  */

object LazySchedulerView {

  implicit class SeqViewConverter[A](f: Seq[A]) {
    val c = Clock.systemDefaultZone()

    class ThisIsMySeqViewWay(val endTime: Instant, ff: Seq[A]) extends SeqView[A, Seq[_]] {
      val seqView = ff.view

      def isBefore(i: Instant):Boolean = c.instant().isBefore(i)

      override def iterator: Iterator[A] = if (isBefore(endTime)) seqView.iterator else Iterator.empty

      override def underlying: Seq[A] = if (isBefore(endTime)) ff else Seq[A]()

      override def apply(i: Int): A = if (isBefore(endTime)) seqView(i) else throw new Exception("Now ThisIsMySeqView is empty")

      override def length: Int = if (isBefore(endTime)) seqView.length else 0
    }

    /**
      *
      * @param expirationTimeout - таймаут, после которого view становится пустым, в миллисекундах
      * @return - view
      */
    def lazySchedule(expirationTimeout: Long): SeqView[A, Seq[_]]  = {
      val i = c.instant().plusMillis(expirationTimeout)
      new ThisIsMySeqViewWay(i, f)
    }
  }
}

object LazySchedulerViewExample extends App {

  import LazySchedulerView._

  val v = List(1, 2, 3, 56)
  val d = v.lazySchedule(1300)
  print(d.length)
  Thread.sleep(1500)
  print(d.length)
}


