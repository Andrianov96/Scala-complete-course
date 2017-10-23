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
    if (newValue > this.value) this.copy(right = Some(this.right.getOrElse(new BSTImpl(newValue)).addBSTImpl(newValue))) else
      this.copy(left = Some(this.left.getOrElse(new BSTImpl(newValue)).addBSTImpl(newValue)))
  }

  def find(value: Int): Option[BST] = {
    if (this.value == value) Some(this) else
    if (value > this.value) this.right.orNull.find(value) else
      this.left.orNull.find(value)
  }

  def putSpaces(number: Int): String = {
    var ret:String = ""
    for (i<-1 to number)
      ret += " "
    ret
  }

  def findDepth: Int = {
    val leftR = if (left.isEmpty) 0 else left.get.findDepth
    val rightR = if (right.isEmpty) 0 else right.get.findDepth
    Math.max(leftR,rightR) + 1
  }

  def myPow(a: Int, b: Int) = {
    var res: Int = 1
    for (i<- 1 to b)
      res *= a
    res
  }

  //This version of toString is restricted
  //It works only with trees that have depth < maxDepth
  //It print correct only on numbers that are less than 1000000
  //spaceArray calculating should be changed to add bigger numbers support
  val maxNumberLenght = 6
  val maxDepth = 10
  val spaceArray = new Array[Int](maxDepth)
  spaceArray(0) = 1
  for (i<-1 until maxDepth){
    spaceArray(i) = spaceArray(i - 1) * 2 + maxNumberLenght / 2
  }


  //return array of strings that look nearly like this
  //    $
  //  $   $
  //$  $ $   $
  def locateStrings() = {
    var res = new Array[String](this.findDepth)
    for (i<-0 until this.findDepth){
      res(i) = ""
    }
    for (i<-0 until this.findDepth){
      res(i) = ""
      for (j<-1 to myPow(2,i))
        res(i) += putSpaces(spaceArray(findDepth - i - 1)) + "$" + putSpaces(spaceArray(findDepth - i - 1))
    }
    res
  }

  //should change $ into given number
  def changeString(str: String, number: String): String = {
    var res = ""
    for (i<- str.indices)
      if (str(i) == ' ') res += str(i) else res += putSpaces(maxNumberLenght - number.length) + number
    res
  }

  def myString(str: Array[String]): Array[String] = {
    var res = new Array[String](str.length)
    for (i<- str.indices){
      res(i) = ""
    }
    res(0) = changeString(str(0), this.value.toString)
    var newStr = str.drop(1)
    var retL = newStr.map(s => s.substring(0, s.length/2))
    var retR = newStr.map(s => s.substring(s.length/2))


    //change "-1" into " " to get printtree without -1
    if (left.isEmpty) retL = retL.map(s => changeString(s, "-1"))
    else retL = left.get.myString(retL)
    if (right.isEmpty) retR = retR.map(s => changeString(s, "-1"))
    else retR = right.get.myString(retR)
    for (i<-0 until str.length - 1)
      res(i + 1) = retL(i) + retR(i)
    res
  }

  override def toString: String = {
    var ret = this.myString(this.locateStrings())
    var res = ret.foldLeft("")(_ + _ + "\r\n")
    res
  }


}

object TreeTest extends App {

  def generator(nodesCount: Int):BST = {
    var res:BST = new BSTImpl((Math.random() * maxValue).toInt)
    for (i<- 2 to nodesCount)
      res = res.add((Math.random() * maxValue).toInt)
    res
  }
  val sc = new java.util.Scanner(System.in)
  val maxValue = 110000
  val nodesCount = sc.nextInt()

  val markerItem = (Math.random() * maxValue).toInt
  val markerItem2 = (Math.random() * maxValue).toInt
  val markerItem3 = (Math.random() * maxValue).toInt

  // Generate huge tree
  val root: BST = BSTImpl(maxValue / 2)
  val tree: BST = generator(nodesCount) // generator goes here

  // add marker items
  val testTree = tree.add(markerItem).add(markerItem2).add(markerItem3)

  // check that search is correct
  require(testTree.find(markerItem).isDefined)
  require(testTree.find(markerItem).isDefined)
  require(testTree.find(markerItem).isDefined)

  println(testTree)
}