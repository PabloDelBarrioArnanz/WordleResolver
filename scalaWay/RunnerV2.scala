
import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using
import scala.util.control.Breaks.*


object RunnerV2 extends App {

  val words5 = ".\\dict\\words5.txt"
  val words = Using(Source.fromFile(words5))

  val green = "green"
  val yellow = "yellow"
  val gray = "gray"

  var startsWith: String = ""
  var endsWith: String = ""
  var contains: Set[Char] = Set()
  var notContains: Set[Char] = Set()

  var tryStartTime = System.nanoTime()

  val matrix = Array.ofDim[WordPosition](6, 5);

  matrix(0)(0) = new WordPosition(Some('c'), Some(gray))
  matrix(0)(1) = new WordPosition(Some('e'), Some(gray))
  matrix(0)(2) = new WordPosition(Some('s'), Some(yellow))
  matrix(0)(3) = new WordPosition(Some('t'), Some(gray))
  matrix(0)(4) = new WordPosition(Some('a'), Some(yellow))

  matrix(1)(0) = new WordPosition(Some('b'), Some(gray))
  matrix(1)(1) = new WordPosition(Some('o'), Some(green))
  matrix(1)(2) = new WordPosition(Some('l'), Some(gray))
  matrix(1)(3) = new WordPosition(Some('s'), Some(green))
  matrix(1)(4) = new WordPosition(Some('a'), Some(green))

  matrix(2)(0) = new WordPosition(Some('r'), Some(gray))
  matrix(2)(1) = new WordPosition(Some('o'), Some(green))
  matrix(2)(2) = new WordPosition(Some('s'), Some(gray))
  matrix(2)(3) = new WordPosition(Some('a'), Some(green))
  matrix(2)(4) = new WordPosition(Some('s'), Some(green))

  matrix(3)(0) = new WordPosition(None, None)
  matrix(3)(1) = new WordPosition(None, None)
  matrix(3)(2) = new WordPosition(None, None)
  matrix(3)(3) = new WordPosition(None, None)
  matrix(3)(4) = new WordPosition(None, None)

  matrix(4)(0) = new WordPosition(None, None)
  matrix(4)(1) = new WordPosition(None, None)
  matrix(4)(2) = new WordPosition(None, None)
  matrix(4)(3) = new WordPosition(None, None)
  matrix(4)(4) = new WordPosition(None, None)

  matrix(5)(0) = new WordPosition(None, None)
  matrix(5)(1) = new WordPosition(None, None)
  matrix(5)(2) = new WordPosition(None, None)
  matrix(5)(3) = new WordPosition(None, None)
  matrix(5)(4) = new WordPosition(None, None)

  println("====================================")
  for (numberTry <- 0 to 6) {
    tryStartTime = System.nanoTime()
    println(s"Try number: $numberTry")
    parseMatrix()
    printConclusion()
    possibleWords(numberTry)
    updateMatrix()
  }

  def parseMatrix(): Unit = {
    for (i <- 0 to 5; j <- 0 to 4) {
      if (j == 0) {
        val row = matrix(i)
        (0 to 4).iterator.takeWhile(indexRow => row(indexRow).color.exists(color => color equals green))
          .foreach(indexRow => startsWith += row(indexRow).letter.map(_.toString).getOrElse(""))
        (4 to 0 by -1).iterator.takeWhile(indexRow => row(indexRow).color.exists(color => color equals green))
          .foreach(indexRow => endsWith = row(indexRow).letter.map(_.toString).getOrElse("") + endsWith)
      }

      val position = matrix(i)(j)
      if (position.color.exists(color => color equals yellow))
        contains += position.letter.get
      else if (position.color.exists(color => color equals gray))
        notContains += position.letter.get
    }
  }

    def possibleWords(numberTry: Int): Unit = {
      if (!numberTry.equals(0))
        println("\t=> Any of this words could be the solution")
        words(_.getLines()
          .filter(word => word.startsWith(startsWith))
          .filter(word => word.endsWith(endsWith))
          .filter(word => contains.forall(word.toSet.contains))
          .filter(word => !notContains.exists(word.toSet.contains))
          .foreach(word => println("\t\t - " + word)))
      else
        println("\tUse any one word like: Cesta")
      println("====================================")
    }

  def updateMatrix(): Unit = {
    startsWith = ""
    endsWith = ""

    println("Time computing try: " + (System.nanoTime() - tryStartTime)/1000 + "\n")
    tryStartTime = System.nanoTime()
  }

  def printConclusion(): Unit = {
    println(s"\tThe word starts with $startsWith")
    println(s"\tThe word ends with $endsWith")
    println(s"\tThe word contains this letters $contains")
    println(s"\tThe word not contains this letters $notContains")
  }
}
