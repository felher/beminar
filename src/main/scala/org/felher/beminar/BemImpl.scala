package org.felher.beminar

import BemFragment.*
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveSvgElement

private[beminar] class BemImpl(fragments: Seq[BemFragment], config: BemConfig) extends Bem {
  override def apply(magnets: BemFragment*): BemImpl            = new BemImpl(fragments ++ magnets, config)
  override def withConfig(config: BemConfig): BemImpl           = new BemImpl(fragments, config)
  override def getConfig: BemConfig                             = config
  override def modifyConfig(f: BemConfig => BemConfig): BemImpl = new BemImpl(fragments, f(config))

  private def fullClassName: Signal[String] = {
    val immediateFragSig: Signal[Seq[BemFragmentImmediate]] = Signal
      .sequence(
        fragments
          .map({
            case fr: BemFragmentImmediate => Signal.fromValue(Seq(fr))
            case fr: BemFragmentSeq       => Signal.fromValue(fr.seq)
            case fr: BemFragmentDynamic   => fr.signal
          })
      )
      .map(_.flatten)

    immediateFragSig.map(frags => BemImpl.toClassNames(frags.toList, config).mkString(" "))
  }

  override def apply(element: ReactiveElement.Base): Unit =
    if (element.isInstanceOf[ReactiveHtmlElement[?]]) {
      (className <-- fullClassName)(implicitly)(element.asInstanceOf[ReactiveHtmlElement.Base])
    } else if (element.isInstanceOf[ReactiveSvgElement[?]]) {
      (svg.className <-- fullClassName)(implicitly)(element.asInstanceOf[ReactiveSvgElement.Base])
    } else {
      throw new IllegalArgumentException("Beminar only supports HTML and SVG elements")
    }
}

private[beminar] object BemImpl {
  private def toClassNames(frags: List[BemFragmentImmediate], config: BemConfig): Vector[String] = {
    final case class PathPart(sep: String, name: String)

    def pathPartFromFrag(frag: BemFragmentImmediate): Option[PathPart] = frag match {
      case part: BemFragmentPart      => Some(PathPart(config.elementSeparator, part.name))
      case mod: BemFragmentBooleanMod => Some(PathPart(config.modifierSeparator, mod.name)).filter(_ => mod.value)
      case mod: BemFragmentStringMod  =>
        Some(PathPart(config.modifierSeparator, mod.name + config.modifierValueSeparator + mod.value))
      case mod: BemFragmentIntMod     =>
        Some(PathPart(config.modifierSeparator, mod.name + config.modifierValueSeparator + mod.value.toString))

      case frag: BemFragmentMacro =>
        frag.frag match {
          case frag: BemFragmentPart       =>
            pathPartFromFrag(
              new BemFragmentPart(BemValRenameStrategy.expandMacroName(frag.name, config.valRenameStrategy))
            )
          case frag: BemFragmentBooleanMod =>
            pathPartFromFrag(
              new BemFragmentBooleanMod(BemValRenameStrategy.expandMacroName(frag.name, config.valRenameStrategy), frag.value)
            )
          case frag: BemFragmentStringMod  =>
            pathPartFromFrag(new BemFragmentStringMod(BemValRenameStrategy.expandMacroName(frag.name, config.valRenameStrategy), frag.value))
          case frag: BemFragmentIntMod     =>
            pathPartFromFrag(new BemFragmentIntMod(BemValRenameStrategy.expandMacroName(frag.name, config.valRenameStrategy), frag.value))
          case frag: BemFragmentMacro      => pathPartFromFrag(frag)
        }

    }

    def append(path: Vector[PathPart], frag: BemFragmentImmediate): Vector[PathPart] =
      pathPartFromFrag(frag).fold(path)(path :+ _)

    def stringifyPath(path: Seq[PathPart]): String =
      path.toList match {
        case Nil          => ""
        case head :: tail => head.name ++ tail.map(part => part.sep + part.name).mkString
      }

    def go(
        prefixes: Vector[Vector[PathPart]],
        mods: Vector[BemFragmentModifier],
        frags: List[BemFragmentImmediate]
    ): Vector[Vector[PathPart]] = {

      def withAllMods =
        prefixes ++ prefixes.flatMap(prefix => mods.map(mod => append(prefix, mod)))

      frags match {
        case Nil => withAllMods

        case (mod: BemFragmentModifier) :: frags => go(prefixes, mods :+ mod, frags)

        case (part: BemFragmentPart) :: frags =>
          val newMods     = if (config.childInheritsModifiers) mods else Vector.empty
          val oldPrefixes = if (config.parentKeepsModifiers) withAllMods else prefixes
          val newPrefixes = oldPrefixes.map(prefix => append(prefix, part))
          go(newPrefixes, newMods, frags)
      }
    }

    go(Vector(Vector.empty), Vector.empty, frags.toList)
      .map(stringifyPath)
      .filter(_.nonEmpty)
  }
}
