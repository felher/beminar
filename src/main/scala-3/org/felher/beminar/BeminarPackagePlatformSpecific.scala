package org.felher.beminar

import com.raquo.laminar.api.L._
import scala.quoted.*
import scala.annotation.targetName

private[beminar] trait BeminarPackagePlatformSpecific {
  /** Uses the _name_ of the variable as the fragment name.
    *
    * Given the val or def `empty: Boolean`, `Bem("/user-list", ~empty)`
    * will macro-expand to `Bem("/user-list", "empty" -> empty)`,
    * allowing you to use the name of the variable directly.
    */
  extension(inline b: Boolean) {
    @targetName("bem_boolean_to_fragment")
    inline def unary_~ : BemFragment =
      ${ BeminarPackagePlatformSpecific.getFragment('b) }
  }

  /** Uses the _name_ of the variable as the fragment name.
    *
    * Given the val or def `color: String`, `Bem("/user-list", ~color)`
    * will macro-expand to `Bem("/user-list", "color" -> color)`,
    * allowing you to use the name of the variable directly.
    */
  extension(inline s: String) {
    @targetName("bem_string_signal_source_to_fragment")
    inline def unary_~ : BemFragment =
      ${ BeminarPackagePlatformSpecific.getFragment('s) }
  }

  /** Uses the _name_ of the variable as the fragment name.
    *
    * Given the val or def `empty: Signal[Boolean]`, `Bem("/user-list", ~empty)`
    * will macro-expand to `Bem("/user-list", "empty" -> empty)`,
    * allowing you to use the name of the variable directly.
    */
  extension(inline sb: SignalSource[Boolean]) {
    @targetName("bem_boolean_signal_source_to_fragment")
    inline def unary_~ : BemFragment =
      ${ BeminarPackagePlatformSpecific.getFragment('sb) }
  }

  /** Uses the _name_ of the variable as the fragment name.
    *
    * Given the val or def `color: Signal[String]`, `Bem("/user-list", ~color)`
    * will macro-expand to `Bem("/user-list", "color" -> color)`,
    * allowing you to use the name of the variable directly.
    */
  extension(inline ss: SignalSource[String]) {
    @targetName("bem_string_signal_source_to_fragment")
    inline def unary_~ : BemFragment =
      ${ BeminarPackagePlatformSpecific.getFragment('ss) }
  }

  /** Uses the _name_ of the variable as the fragment name.
    *
    * Given the val or def `size: Signal[Int]`, `Bem("/user-list", ~size)`
    * will macro-expand to `Bem("/user-list", "size" -> size)`,
    * allowing you to use the name of the variable directly.
    */
  extension(inline si: SignalSource[Int]) {
    @targetName("bem_int_signal_source_to_fragment")
    inline def unary_~ : BemFragment =
      ${ BeminarPackagePlatformSpecific.getFragment('si) }
  }
}

private [beminar] object BeminarPackagePlatformSpecific {
  def getFragment[A: Type](expr: Expr[A])(using q: Quotes): Expr[BemFragment] =
    import q.reflect.*
    val name = expr.asTerm match {
      case Inlined(_, _, Ident(name)) => name
      case Inlined(_, _, Select(_, name)) => name
      case _ =>
        val tree = expr.asTerm.show(using Printer.TreeStructure)
        report.errorAndAbort(s"We can currently only inline the names of vals, nullary functions or simple selects, but got $tree", expr)
    }

    val convExpr = Implicits.search(TypeRepr.of[((String, A)) => BemFragment]) match
      case _: ImplicitSearchFailure => report.errorAndAbort("No implicit conversion found for the given type.", expr)
      case s: ImplicitSearchSuccess => s.tree.asExprOf[((String, A)) => BemFragment]

    '{
      val conv = $convExpr
      val fragment: BemFragment = conv((${Expr(name)}, $expr))
      BemFragment.markForNameTransformation(fragment)
    }
}
