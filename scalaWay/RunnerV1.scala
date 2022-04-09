
import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using


object RunnerV1 extends App {

  val allWords = ".\\dict\\allWords.txt"
  val words5 = ".\\dict\\words5.txt"

  val startsWith = ""
  val endsWith = "rto"
  val contains = List('t', 'o', 'r')
  val notContains = List('á','é','í','ó','ú','c','e','s','a', 'b', 'o')

  Using(Source.fromFile(words5))(_.getLines()
    .filter(word => word.startsWith(startsWith))
    .filter(word => word.endsWith(endsWith))
    .filter(word => contains.forall(word.toSet.contains))
    .filter(word => !notContains.exists(word.toSet.contains))
    .filter(word => !word.toSet(3).equals('t'))
    .filter(word => !word.toSet(1).equals('c'))
    .filter(word => !word.toSet(1).equals('b'))
    .filter(word => !word.toSet(2).equals('e'))
    .filter(word => !word.toSet(3).equals('s'))
    .filter(word => !word.toSet(4).equals('a'))
    .foreach(println))
}
