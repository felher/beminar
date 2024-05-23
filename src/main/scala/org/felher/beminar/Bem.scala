package org.felher.beminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement

/** Represents a BEM entity.
  */
trait Bem extends Modifier[ReactiveElement.Base] {

  /** Adds fragments to this BEM entity, yielding a new BEM entity.
    *
    * You can repeatedly add fragments to the BEM entity:
    * {{{
    * val block = Bem("/user-list", "empty" -> isEmpty)
    * div(
    *   block,
    *   items.map(item => div(block, "/item", "hidden" -> item.hidden))
    * )
    * }}}
    *
    * See the [[BemFragment$ BemFragment companion object]] for a list of all the different ways to add fragments.
    *
    * @param magnets the fragments to add, like `"/user-list"` or `"hidden" -> true`.
    */
  def apply(magnets: BemFragment*): Bem

  /** Creates a new BEM entity with the given output configuration. */
  def withConfig(config: BemConfig): Bem

  /** Returns the current output configuration. */
  def getConfig: BemConfig

  /** Creates a new BEM entity with the output configuration modified by the given function. */
  def modifyConfig(f: BemConfig => BemConfig): Bem
}

object Bem {

  /** Creates a new BEM entity with the given fragments.
    *
    * For example:
    * {{{
    * val block = Bem("/user-list", "empty" -> isEmpty)
    * }}}
    *
    * See the [[BemFragment$ BemFragment companion object]] for a list of all the different ways to add fragments.
    *
    * @param magnets the fragments to add, like `"/user-list"` or `"hidden" -> true`.
    */
  def apply(magnets: BemFragment*): Bem = new BemImpl(magnets, BemConfig.default)
}
