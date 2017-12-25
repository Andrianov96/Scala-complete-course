package lectures.oop.types

import lectures.matching.SortingStuff.Watches

/**
  * Модифицируйте реализацию BSTImpl из предыдущего задания.
  * Используя тайп параметры и паттерн Type Class, реализуйте GeneralBSTImpl таким образом,
  * чтобы дерево могло работать с произвольным типом данных.
  *
  * Наследников GeneralBSTImpl определять нельзя.
  *
  * Создайте генератор для деревьев 3-х типов данных:
  * * * * float
  * * * * String
  * * * * Watches из задачи SortStuff. Большими считаются часы с большей стоимостью
  */



trait GeneralBST[T] {
  val value: T
  val left: Option[GeneralBST[T]]
  val right: Option[GeneralBST[T]]

  def add(newValue: T): GeneralBST[T]

  def find(value: T): Option[GeneralBST[T]]
}

trait TypeForBST[T] extends Ordering[T]{
  def compare(f: T, s: T): Int
}



class GeneralBSTImpl[T: TypeForBST](val value: T, val left: Option[GeneralBST[T]] = None, val right: Option[GeneralBST[T]] = None) extends GeneralBST[T] {
  val comparator = implicitly[TypeForBST[T]]

  override def find(valueToFind: T) : Option[GeneralBST[T]] = {
    if (comparator.compare(valueToFind, value) < 0) this.left.map(_.find(valueToFind)).getOrElse(None)
    else if (comparator.compare(valueToFind, value) > 0) this.right.map(_.find(valueToFind)).getOrElse(None)
    else Some(this)
  }

  override def add(newValue: T): GeneralBST[T] = {
    if (comparator.compare(newValue, value) < 0) new GeneralBSTImpl(value, left, Some(right.map(_.add(newValue)).getOrElse(new GeneralBSTImpl[T](newValue))))
      else if (comparator.compare(newValue, value) > 0) new GeneralBSTImpl(value, Some(left.map(_.add(newValue)).getOrElse(new GeneralBSTImpl[T](newValue))), right)
        else this
  }
}

object ImplicitsDeclaration extends App{
  implicit val float2BST: TypeForBST[Float] = new TypeForBST[Float] {
    def compare(f: Float, s: Float) = f compare s
  }

  implicit val string2BST: TypeForBST[String] = new TypeForBST[String] {
    def compare(f: String, s: String) = f compare s
  }

  implicit val watches2BST: TypeForBST[Watches] = new TypeForBST[Watches] {
    def compare(f: Watches, s: Watches) = f.cost compare s.cost
  }
}