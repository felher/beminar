package org.felher.beminar

/** Represents the strategy of how to transform the name of a val or a def into
  * a CSS class fragment when the name of the variable is directly used by
  * scala 3 only extensions.
  *
  * You can see the strategies on the [[BemValRenameStrategy$ companion object]].
  */
sealed trait BemValRenameStrategy

/** Represents the strategy of how to transform the name of a val or a def into
  * a CSS class fragment when the name of the variable is directly used by
  * scala 3 only extensions.
  */
object BemValRenameStrategy {
  final private class Custom(val f: String => String) extends BemValRenameStrategy

  /** Keeps the name of the val or def as is.
    * 
    * For example, `val isEmpty: Boolean` will just be `--isEmpty` in the CSS
    * class (given that the separator is `--`).
    */
  val asIs: BemValRenameStrategy = new Custom(identity)

  /** Transforms the name of the val or def into kebab-case.
    * 
    * For example, `val isEmpty: Boolean` will be `--is-empty` in the CSS
    * class (given that the separator is `--`).
    */
  val kebab: BemValRenameStrategy = new Custom(_.flatMap(c => if (c.isUpper) "-" + c.toLower else c.toString))

  /** Transforms the name of the val or def into snake_case.
    * 
    * For example, `val isEmpty: Boolean` will be `--is_empty` in the CSS
    * class (given that the separator is `--`).
    */
  val snake: BemValRenameStrategy = new Custom(_.flatMap(c => if (c.isUpper) "_" + c.toLower else c.toString))

  /** Transforms the name of the val or def using a custom function.
    * 
    * For example, given `custom(_.toUpperCase)` and `val isEmpty: Boolean`
    * the css class will be `--ISEMPTY` (given that the separator is `--`).
    */
  def custom(f: String => String): BemValRenameStrategy = new Custom(f)

  private[beminar] def expandMacroName(name: String, strategy: BemValRenameStrategy): String =
    strategy match {
      case s: BemValRenameStrategy.Custom => s.f(name)
    }
}
