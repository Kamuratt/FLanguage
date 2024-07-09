package br.unb.cic.flang

import Declarations._
import Substitution._

import ErrorMonad._

object Interpreter {
  def eval(expr: Expr, declarations: List[FDeclaration]): M[Integer] =
    expr match {
      case CInt(v) => pure(v)
      case Add(lhs, rhs) =>
        flatMap(eval(lhs, declarations))({ a: Integer =>
          flatMap(eval(rhs, declarations))({ b: Integer => pure(a + b) })
        })
      case Mul(lhs, rhs) =>
        flatMap(eval(lhs, declarations))({ a: Integer =>
          flatMap(eval(rhs, declarations))({ b: Integer => pure(a * b) })
        })
      case Id(_) =>
        err("Not expecting a variable while executing the interpreter")
      case App(n, e) =>
        flatMap(lookup(n, declarations))({ f: FDeclaration =>
          {
            val bodyS = substitute(e, f.arg, f.body)
            eval(bodyS, declarations)
          }
        })
    }
}
