package lectures.oop

import scala.collection.mutable.ArrayBuffer


/**
  * BSTImpl - это бинарное дерево поиска, содержащее только значения типа Int
  *
  * * Оно обладает следующими свойствами:
  * * * * * левое поддерево содержит значения, меньшие значения родителя
  * * * * * правое поддерево содержит значения, большие значения родителя
  * * * * * значения, уже присутствующие в дереве, в него не добавляются
  * * * * * пустые значения (null) не допускаются
  *
  * * Завершите реализацию методов кейс класс BSTImpl:
  * * * * * Трейт BST и BSTImpl разрешается расширять любым образом
  * * * * * Изменять сигнатуры классов и методов, данные в условии, нельзя
  * * * * * Постарайтесь не использовать var и мутабильные коллекции
  * * * * * В задаче про распечатку дерева, нужно раскомментировать и реализовать метод toString()
  *
  * * Для этой структуры нужно реализовать генератор узлов.
  * * Генератор:
  * * * * * должен создавать дерево, содержащее nodesCount узлов.
  * * * * * не должен использовать переменные или мутабильные структуры.
  *
  */
trait BST {
  val value: Int
  val left: Option[BST]
  val right: Option[BST]

  def add(newValue: Int): BST

  def find(value: Int): Option[BST]
}

case class BSTImpl(value: Int,
                   left: Option[BSTImpl] = None,
                   right: Option[BSTImpl] = None) extends BST {


  def add(newValue: Int): BST = {
    this.addBSTImpl(newValue)
  }

  def addBSTImpl(newValue: Int) :BSTImpl = {
    if (newValue == this.value) this else
      if (newValue > this.value) this.copy(right = Some(this.right.map(_.addBSTImpl(newValue)).getOrElse(new BSTImpl(newValue)))) else
      this.copy(left = Some(this.left.map(_.addBSTImpl(newValue)).getOrElse(new BSTImpl(newValue))))
  }

  def find(value: Int): Option[BST] = {
    if (this.value == value) Some(this) else
      if (value > this.value) this.right.flatMap(_.find(value)) else
      this.left.flatMap(_.find(value))
  }

  val additionSpaces = " " * 2

  def addSpaces(leftAr: Array[String], rightAr: Array[String], valueLength: Int): Array[String] = {
    var res = new ArrayBuffer[String]()
    val separator = additionSpaces + " " * valueLength + additionSpaces
    if (leftAr.length < rightAr.length){
      for (i <- leftAr.indices){
        res += leftAr(i) + separator + rightAr(i)
      }
      for (i <- leftAr.length until rightAr.length) {
        res += " " * leftAr(0).length + separator + rightAr(i)
      }
    } else {
      for (i <- rightAr.indices){
        res += leftAr(i) + separator + rightAr(i)
      }
      for (i <- rightAr.length until leftAr.length) {
        res += leftAr(i) + separator + " " * rightAr(0).length
      }
    }
    res.toArray
  }

  def toStr (): Array[String] = {
    (left, right) match {
      case (None, None) => Array[String](value.toString)
      case (Some(leftT), Some(rightT)) => {
        val leftAr = left.map{ _.toStr()}.getOrElse(Array[String](""))
        val rightAr = right.map{ _.toStr()}.getOrElse(Array[String](""))
        val valueStr = value.toString
        val leafsAr = addSpaces(leftAr, rightAr, valueStr.length)
        val firstStr = " " * leftAr(0).length + additionSpaces + valueStr + additionSpaces + " " * rightAr(0).length
        Array[String](firstStr)  ++ leafsAr
      }
      case (Some(leftT), None) => {
        val leftAr = leftT.toStr()
        val valueStr = value.toString
        val leafsAr = addSpaces(leftAr, Array[String](""), valueStr.length)
        val firstStr = " " * leftAr(0).length + additionSpaces + valueStr + additionSpaces
        Array[String](firstStr)  ++ leafsAr
      }
      case (None, Some(rightT)) => {
        val rightAr = rightT.toStr()
        val valueStr = value.toString
        val leafsAr = addSpaces(Array[String](""), rightAr, valueStr.length)
        val firstStr = additionSpaces + valueStr + additionSpaces + " " * rightAr(0).length
        Array[String](firstStr)  ++ leafsAr
      }
    }
  }

  override def toString: String = {
    var res = ""
    val ar = this.toStr()
    for (i<- ar.indices){
      res += ar(i) + "\r\n"
    }
    res
  }

}

object TreeTest extends App {

//  def generator(nodesCount: Int):BST = {
//    var res:BST = new BSTImpl((Math.random() * maxValue).toInt)
//    for (i<- 2 to nodesCount)
//      res = res.add((Math.random() * maxValue).toInt)
//    res
//  }
//  val sc = new java.util.Scanner(System.in)
//  val maxValue = 110000
//  val nodesCount = sc.nextInt()
//
//  val markerItem = (Math.random() * maxValue).toInt
//  val markerItem2 = (Math.random() * maxValue).toInt
//  val markerItem3 = (Math.random() * maxValue).toInt
//
//  // Generate huge tree
//  val root: BST = BSTImpl(maxValue / 2)
//  val tree: BST = generator(nodesCount) // generator goes here
//
//  // add marker items
//  val testTree = tree.add(markerItem).add(markerItem2).add(markerItem3)
//
//  // check that search is correct
//  require(testTree.find(markerItem).isDefined)
//  require(testTree.find(markerItem).isDefined)
//  require(testTree.find(markerItem).isDefined)

  val myTestTree = new BSTImpl(10)
  val test = myTestTree.add(9).add(15).add(6).add(8).add(7).add(3).add(4).add(5).add(2).add(1).add(13).add(18).add(11).add(12).add(16)
  println(test.toString)
}