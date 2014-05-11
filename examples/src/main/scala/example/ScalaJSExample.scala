package example

import scala.scalajs.js
import js.Dynamic.{ global ⇒ g }
import js.annotation.JSExport

import org.parboiled2._
import scala.annotation.tailrec
import shapeless._

class ABCParser(val input: ParserInput) extends Parser {
  def InputLine = rule { "ab" }
}

@JSExport
object ScalaJSExample {
  @JSExport
  def main(): Unit = {
    val abcParser = new ABCParser("ab")
    val res = abcParser.run(_.InputLine) match {
      case Right(_)  ⇒ "matched"
      case Left(err) ⇒ "failed-to-match"
    }

    val paragraph = g.document.createElement("p")
    paragraph.innerHTML = s"<strong>It works! ${res}</strong>"
    g.document.getElementById("playground").appendChild(paragraph)
  }
}
