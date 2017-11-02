package lectures.functions

import org.scalatest.{FlatSpec, Matchers}

class FibonacciTest extends FlatSpec with Matchers {
  "Fibbonaci" should "be equal 1 on input 1" in{
    var res = Fibonacci.fibs(1)
    res should be(1)
  }

  "Fibbonaci" should "be equal 8 on input 6" in {
    var res = Fibonacci.fibs(6)
    res should be(8)
  }

  "Fibbonaci" should "throw SOFE if passed a negative value" in {
    an[StackOverflowError] should be thrownBy{
      Fibonacci.fibs(-1)
    }
  }

  "Fibbonaci" should "throw SOFE if passed a zero value" in {
    an[StackOverflowError] should be thrownBy{
      Fibonacci.fibs(0)
    }
  }
}
