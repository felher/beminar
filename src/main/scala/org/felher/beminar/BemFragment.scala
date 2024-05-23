package org.felher.beminar

import com.raquo.laminar.api.L._

/** Represents a fragment of a BEM entity.
  *
  * Used as a magnet in the magnet pattern to allow for a flexible DSL.
  *
  * Look at [[BemFragment$ the companion object]] for a list of all the supported fragments
  */
sealed trait BemFragment {}

/** Contains implicit conversions for the DSL, most importantly [[fromString]].
  *
  * In general, there is the base version of an implicit conversion, for example accepting a `Tuple[String, Boolean]`.
  * Then there is a version where the value part is wrapped in a signal(source), for example accepting a `Tuple[String, Signal[Boolean]]`, the name of which ends with an `S`.
  * Finally, there is a version where the whole construct is wrapped in a signal(source), for example accepting a `Signal[Tuple[String, Boolean]]`, the name of which ends with an `SS`.
  */
object BemFragment {
  sealed private[beminar] trait BemFragmentImmediate extends BemFragment
  sealed private[beminar] trait BemFragmentModifier  extends BemFragmentImmediate

  private[beminar] class BemFragmentPart(val name: String)                           extends BemFragmentImmediate
  private[beminar] class BemFragmentBooleanMod(val name: String, val value: Boolean) extends BemFragmentModifier
  private[beminar] class BemFragmentStringMod(val name: String, val value: String)   extends BemFragmentModifier

  private[beminar] class BemFragmentDynamic(val signal: Signal[Seq[BemFragmentImmediate]]) extends BemFragment
  private[beminar] class BemFragmentSeq(val seq: Seq[BemFragmentImmediate])                extends BemFragment

  private def parseString(s: String): BemFragmentImmediate = {
    if (s.startsWith("/")) new BemFragmentPart(s.substring(1))
    else new BemFragmentBooleanMod(s, true)
  }

  /** Parses a string into either a block or element name or a boolean modifier.
    *
    * If the string starts with a `/`, it is interpreted as a block or element name.
    * Otherwise, it is interpreted as a boolean modifier with the value `true`.
    */
  implicit def fromString(s: String): BemFragment = parseString(s)

  /** Same as [[fromString]], but the value is wrapped in a signal. */
  implicit def fromStringS(s: SignalSource[String]): BemFragment = new BemFragmentDynamic(
    s.toObservable.map(s => List(parseString(s)))
  )

  /** Adds a modifier like `("hidden" -> true)` to a BEM entity.
    *
    * If the second entry in the tuple is true, the modifier is added, otherwise
    * nothing more is emitted.
    */
  implicit def fromBooleanTuple(t: (String, Boolean)): BemFragment = new BemFragmentBooleanMod(t._1, t._2)

  /** Same as [[fromBooleanTuple]], but the value-part is wrapped in a signal. */
  implicit def fromBooleanTupleS(t: (String, SignalSource[Boolean])): BemFragment = new BemFragmentDynamic(
    t._2.toObservable.map(b => List(new BemFragmentBooleanMod(t._1, b)))
  )

  /** Same as [[fromBooleanTuple]], but the whole construct is wrapped in a signal. */
  implicit def fromBooleanTupleSS(t: SignalSource[(String, Boolean)]): BemFragment = new BemFragmentDynamic(
    t.toObservable.map(t => List(new BemFragmentBooleanMod(t._1, t._2)))
  )

  /** Adds a modifier with a value, like `("color" -> "red")` to a BEM entity. */
  implicit def fromStringTuple(t: (String, String)): BemFragment = new BemFragmentStringMod(t._1, t._2)

  /** Same as [[fromStringTuple]], but the value-part is wrapped in a signal. */
  implicit def fromStringTupleS(t: (String, SignalSource[String])): BemFragment = new BemFragmentDynamic(
    t._2.toObservable.map(s => List(new BemFragmentStringMod(t._1, s)))
  )

  /** Same as [[fromStringTuple]], but the whole construct is wrapped in a signal. */
  implicit def fromStringTupleSS(t: SignalSource[(String, String)]): BemFragment = new BemFragmentDynamic(
    t.toObservable.map(t => List(new BemFragmentStringMod(t._1, t._2)))
  )

  /** Adds multiple value-modifiers like `Map("size" -> "large", "color" -> "red")` to a BEM entity. */
  implicit def fromStringMap(m: Map[String, String]): BemFragment = new BemFragmentSeq(
    m.toList.map(t => new BemFragmentStringMod(t._1, t._2))
  )

  /** Same as [[fromStringMap]], but the value-part is wrapped in a signal. */
  implicit def fromStringMapS(m: Map[String, SignalSource[String]]): BemFragment = new BemFragmentDynamic(
    Signal.sequence(m.toList.map(t => t._2.toObservable.map(v => new BemFragmentStringMod(t._1, v))))
  )

  /** Same as [[fromStringMap]], but the whole construct is wrapped in a signal. */
  implicit def fromStringMapSS(m: SignalSource[Map[String, String]]): BemFragment = new BemFragmentDynamic(
    m.toObservable.map(m => m.toList.map(t => new BemFragmentStringMod(t._1, t._2)))
  )

  /** Adds multiple boolean-modifiers like `Map("hidden" -> true, "disabled" -> false)` to a BEM entity.
    * 
    * Emits nothing for the modifiers with the value `false`.
    **/
  implicit def fromBooleanMap(m: Map[String, Boolean]): BemFragment = new BemFragmentSeq(
    m.toList.map(t => new BemFragmentBooleanMod(t._1, t._2))
  )

  /** Same as [[fromBooleanMap]], but the value-part is wrapped in a signal. */
  implicit def fromBooleanMapS(m: Map[String, SignalSource[Boolean]]): BemFragment = new BemFragmentDynamic(
    Signal.sequence(m.toList.map(t => t._2.toObservable.map(v => new BemFragmentBooleanMod(t._1, v))))
  )

  /** Same as [[fromBooleanMap]], but the whole construct is wrapped in a signal. */
  implicit def fromBooleanMapSS(m: SignalSource[Map[String, Boolean]]): BemFragment = new BemFragmentDynamic(
    m.toObservable.map(m => m.toList.map(t => new BemFragmentBooleanMod(t._1, t._2)))
  )
}
