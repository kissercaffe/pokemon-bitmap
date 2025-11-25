package bitmap

import scala.collection.immutable.BitSet
import scala.collection.mutable

import parser.{LogicalExpr, Term, Not, And, Or, Diff, Xor}
import parser.LogicalExprParser


class BitMap[T] {
    private val bitmaps = mutable.Map[T, BitSet]()
    private lazy val universe = bitmaps.values.fold(BitSet.empty)(_ | _)

    def add(key: T, index: Int) = {
      val current = bitmaps.getOrElse(key, BitSet.empty)
      bitmaps(key) = current + index
    }

    def addAll(key: T, indices: List[Int]) = {
      val current = bitmaps.getOrElse(key, BitSet.empty)
      bitmaps(key) = current ++ indices
    }

    def getBitSet(key: T): BitSet = {
      bitmaps.getOrElse(key, BitSet.empty)
    }

    def search(key: T): List[Int] = {
      bitmaps.getOrElse(key, BitSet.empty).toList
    }

    def evaluate(expr: LogicalExpr): BitSet = {
        expr match {
            case Term(key) => bitmaps.getOrElse(key.asInstanceOf[T], BitSet.empty)
            case Not(expr) => universe &~ evaluate(expr)
            case And(left, right) => evaluate(left) & evaluate(right)
            case Or(left, right) => evaluate(left) | evaluate(right)
            case Diff(left, right) => evaluate(left) &~ evaluate(right)
            case Xor(left, right) => evaluate(left) ^ evaluate(right)
        }
    }

    def query(queryString: String): BitSet = {
        val parser = new LogicalExprParser()
        parser.parse(queryString).map(evaluate).getOrElse(BitSet.empty)
    }
}

