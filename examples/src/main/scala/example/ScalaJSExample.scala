package example

import scala.scalajs.js
import js.Dynamic.{ global ⇒ g }
import js.annotation.JSExport

import org.parboiled2._
import scala.annotation.tailrec
import shapeless._

// `The classic non-context-free language <http://en.wikipedia.org/wiki/Parsing_expression_grammar#Examples>`_
// .. math:: \{ a^n b^n c^n : n \ge 1 \}

class ABCParser(val input: ParserInput) extends Parser {
  def InputLine = rule { "ab" }

  //  def InputLine = rule { &(A ~ "c") ~ oneOrMore("a") ~ B ~ !("a" | "b" | "c") ~ EOI }
  //
  //  def A: Rule0 = rule { "a" ~ optional(A) ~ "b" }
  //
  //  def B: Rule0 = rule { "b" ~ optional(B) ~ "c" }
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
