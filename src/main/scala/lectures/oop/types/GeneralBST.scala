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

trait TypeForBST[T] {
  def isGreater(f: T, s: T): Boolean
  def isLess(f: T, s: T): Boolean
}

class GeneralBSTImpl[T: TypeForBST](val value: T, val left: Option[GeneralBST[T]] = None, val right: Option[GeneralBST[T]] = None) extends GeneralBST[T] {
  val r = implicitly[TypeForBST[T]]

  override def find(valueToFind: T): Option[GeneralBST[T]] = {
    if (r.isLess(valueToFind, value)) this.left.map(_.find(valueToFind)).getOrElse(None)
    else if (r.isGreater(valueToFind, value)) this.right.map(_.find(valueToFind)).getOrElse(None)
    else Some(this)
  }

  override def add(newValue: T): GeneralBST[T] = {
    if (r.isGreater(newValue, value)) new GeneralBSTImpl(value, left, Some(right.map(_.add(newValue)).getOrElse(new GeneralBSTImpl[T](newValue))))
      else if (r.isLess(newValue, value)) new GeneralBSTImpl(value, Some(left.map(_.add(newValue)).getOrElse(new GeneralBSTImpl[T](newValue))), right)
        else this
  }
}

object ImplicitsDeclaration extends App{
  implicit val float2BST: TypeForBST[Float] = new TypeForBST[Float] {
    override def isGreater(f: Float, s: Float): Boolean = f > s

    override def isLess(f: Float, s: Float): Boolean = f < s
  }

  implicit val string2BST: TypeForBST[String] = new TypeForBST[String] {
    override def isGreater(f: String, s: String): Boolean = f > s

    override def isLess(f: String, s: String): Boolean = f < s
  }

  implicit val watches2BST: TypeForBST[Watches] = new TypeForBST[Watches] {
    override def isGreater(f: Watches, s: Watches): Boolean = (f, s) match {
      case (a, b) if a.brand > b.brand => true
      case (a, b) if a.brand == b.brand && a.cost > b.cost => true
      case _ => false
    }

    override def isLess(f: Watches, s: Watches): Boolean = (f, s) match {
      case (a, b) if a.brand < b.brand => true
      case (a, b) if a.brand == b.brand && a.cost < b.cost => true
      case _ => false
    }
  }
}