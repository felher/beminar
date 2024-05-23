package org.felher

/** The Beminar library provides a low-boilerplate way to create BEM entities for Laminar.
  * 
  * The library consists of three main parts:
  *   - the [[Bem]] trait, which represents a BEM entity
  *   - the [[BemFragment]] trait, which represents a fragment of a BEM entity, like a modifier or a block
  *   - the [[BemConfig]] trait, which represents the output configuration for BEM class generation
  *
  * Here is a small example of how a simple Laminar counter component could look like with Beminar:
  *
  * {{{
  * import com.raquo.laminar.api.L.*
  * import org.felher.beminar.Bem
  * 
  * object Counter:
  *   def render(isFrozen: Signal[Boolean]): HtmlElement =
  *     // this sets up the BEM entity, "disabled" is inherited by the children
  *     val bem   = Bem("/counter", "frozen" -> isFrozen)
  *     val count = Var(0)
  * 
  *     div(
  *       bem,
  *       div(
  *         bem("/count", "wow" -> count.signal.map(_ > 9)),
  *         child.text <-- count.signal.map(_.toString)
  *       ),
  *       button(
  *         disabled <-- isFrozen,
  *         bem("/increment"),
  *         "Increment",
  *         onClick --> (_ => count.update(_ + 1))
  *       ),
  *       button(
  *         disabled <-- isFrozen,
  *         bem("/reset"),
  *         "Reset",
  *         onClick --> (_ => count.set(0))
  *       )
  *     )
  * }}}
  *
  * Here is how the scss file for it would look like:
  * {{{
  * .counter {
  *   display: grid;
  *   grid-template-columns: repeat(2, minmax(0, 1fr));
  *   gap: 1rem;
  * 
  *   &__count {
  *     grid-column: 1 / 3;
  *     text-align: center;
  * 
  *     &--wow {
  *       color: red;
  *     }
  *   }
  * 
  *   &__increment,
  *   &__reset {
  *     padding: 0.5rem 1rem;
  *     border: 1px solid black;
  *     border-radius: 5px;
  *     text-align: center;
  * 
  *     &--frozen {
  *       background-color: gray;
  *       color: white;
  *       cursor: not-allowed;
  *     }
  *   }
  * }
  * }}}
  *
  * `frozen` is inherited as modifier by the children of `counter`, so it can be used both on `.counter` as well as on <code>.counter__reset</code>.
  * If you don't want that, you can disable it by changing the output configuration via [[Bem.modifyConfig]].
  *
  * If we have more than 9 clicks, the `wow` modifier is added to the <code>.counter__count</code> element and it turns red.
  */
package object beminar {}
