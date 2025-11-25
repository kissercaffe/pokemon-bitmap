package parser

import scala.util.parsing.combinator._

sealed trait LogicalExpr
case class Term(key: String) extends LogicalExpr
case class And(left: LogicalExpr, right: LogicalExpr) extends LogicalExpr
case class Or(left: LogicalExpr, right: LogicalExpr) extends LogicalExpr
case class Not(expr: LogicalExpr) extends LogicalExpr
case class Diff(left: LogicalExpr, right: LogicalExpr) extends LogicalExpr      // 差集合 A - B
case class Xor(left: LogicalExpr, right: LogicalExpr) extends LogicalExpr 

class LogicalExprParser extends RegexParsers {

    override def skipWhitespace = true

    def expr: Parser[LogicalExpr] = orExpr

    def orExpr: Parser[LogicalExpr] = {
        xorExpr ~ rep(("|" | "or" | "OR") ~ xorExpr) ^^ {
            case x ~ xs => xs.foldLeft(x)((left, opRight) => Or(left, opRight._2))
        }
    }

    def xorExpr: Parser[LogicalExpr] = {
        diffExpr ~ rep(("^" | "xor" | "XOR") ~ diffExpr) ^^ {
            case x ~ xs => xs.foldLeft(x)((left, opRight) => Xor(left, opRight._2))
        }
    }

    def diffExpr: Parser[LogicalExpr] = {
        andExpr ~ rep(("-" | "diff" | "DIFF") ~ andExpr) ^^ {
            case x ~ xs => xs.foldLeft(x)((left, opRight) => Diff(left, opRight._2))
        }
    }

    def andExpr: Parser[LogicalExpr] = {
        notExpr ~ rep(("&" | "and" | "AND") ~ notExpr) ^^ {
            case x ~ xs => xs.foldLeft(x)((left, opRight) => And(left, opRight._2))
        }
    }

    def notExpr: Parser[LogicalExpr] = {
        rep(("!" | "not" | "NOT")) ~ primary ^^ {
            case xs ~ expr => xs.foldLeft(expr)((e, _) => Not(e))
        }
    }

    def primary: Parser[LogicalExpr] = {
        "(" ~> expr <~ ")" | term
    }

    def term: Parser[LogicalExpr] = {
        """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ { s => Term(s) }
    }

    def parse(input: String): Either[String, LogicalExpr] = {
        parseAll(expr, input) match {
            case Success(result, _) => Right(result)
            case failure: NoSuccess => Left(s"Failed to parse expression: ${failure.msg} at position ${failure.next.pos}")
        }
    }
}

