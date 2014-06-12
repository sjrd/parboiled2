package org.parboiled2.examples
package js

import org.parboiled2._

import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

abstract class JSonTerm
case class JSonObject(members: Seq[JSonPair]) extends JSonTerm
case class JSonArray(elements: Seq[JSonTerm]) extends JSonTerm
case class JSonPair(str: String, value: JSonTerm) extends JSonTerm
case class JSonString(str: String) extends JSonTerm
case class JSonNumber(num: Int) extends JSonTerm
case class JSonBoolean(f: Boolean) extends JSonTerm

class JsonSimpleParser(val input: ParserInput) extends Parser {
  def InputLine = rule { Object ~ EOI }

  def Object = rule {
    ("{" ~ optional(Members) ~ "}") ~>
      ((x: Option[List[JSonPair]]) ⇒ JSonObject(x.getOrElse(List())))
  }

  def Members: Rule1[List[JSonPair]] = rule { (Pair ~> (List(_))) ~ zeroOrMore("," ~ Pair ~> ((_: List[JSonPair]) :+ _)) }

  def Pair = rule { String ~> ((x: JSonString) ⇒ x.str) ~ ":" ~ Value ~> (JSonPair((_: String), (_: JSonTerm))) }

  def Array: Rule1[JSonArray] = rule {
    "[" ~ Value ~ zeroOrMore("," ~ Value) ~>
      ((x: JSonTerm, y: Seq[JSonTerm]) ⇒ JSonArray(x +: y)) ~ "]"
  }

  def String = rule { "\"" ~ capture(zeroOrMore(Char)) ~> JSonString ~ "\"" }

  def Char = rule { "a" - "z" | "A" - "Z" | Digit }

  def Value: Rule1[JSonTerm] = rule { String | Number | Array | Boolean | Object }

  def Boolean: Rule1[JSonTerm] = rule {
    (str("true") ~ push(true) ~> JSonBoolean) |
      (capture("false") ~> ((x: String) ⇒ JSonBoolean(false)))
  }

  def Number = rule { capture(Integer) ~> ((x: String) ⇒ JSonNumber(x.toInt)) }

  def Integer = rule { optional("-") ~ (("1" - "9") ~ zeroOrMore(Digit) | oneOrMore(Digit)) }

  def Digit = rule { "0" - "9" }
}

@JSExport
object JsonParserJS {
  @JSExport
  def parse(input: String): String = {
    val parser = new JsonSimpleParser(input)
    parser.InputLine.run() match {
      case Success(x)             ⇒ "Expression is valid: " + x
      case Failure(e: ParseError) ⇒ "Expression is not valid: " + parser.formatError(e)
      case Failure(e)             ⇒ "Unexpected error during parsing run: " + e.getStackTrace.mkString("<br/>")
    }
  }
}
