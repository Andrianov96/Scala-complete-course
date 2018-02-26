package lectures.collections

/**
  * Постарайтесь не использовать мутабильные коллекции и var
  * Подробнее о сортировке можно подсмотреть здесь - https://en.wikipedia.org/wiki/Merge_sort
  *
  */
object MergeSortImpl extends App {

  def merge(first: Seq[Int], second: Seq[Int], ret :Seq[Int] = Seq()): Seq[Int] =
    (first, second) match {
      case (Nil, _) => ret ++ second
      case (_, Nil) => ret ++ first
      case (l::left, r::right) =>
        if (l < r) merge(left, second, ret :+ l) else
          merge(first, right, ret :+ r)
    }

  def mergeSort(data: Seq[Int]): Seq[Int] = {
    if (data.length == 1) data else {
      val (firstHalf, secondHalf) = data.splitAt(data.length / 2)
      merge(mergeSort(firstHalf),mergeSort(secondHalf))
    }
  }
}
