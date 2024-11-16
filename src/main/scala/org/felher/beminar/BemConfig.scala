package org.felher.beminar

/** Represents a configuration for BEM class generation.
  */
trait BemConfig {

  /** The separator between the block and the element, like <code> __ </code> in <code>user-list__item</code>.
    * The default is <code>__</code>.
    */
  def elementSeparator: String = "__"

  /** The separator between the block/element and the modifier, like `--` in `button--deactivated`.
    * The default is `--`.
    */
  def modifierSeparator: String = "--"

  /** The separator between the modifier and its value, like `-` in `button--size-small`.
    * The default is `-`.
    */
  def modifierValueSeparator: String = "-"

  /** Whether the parent block/element should keep its modifiers when a child element is appended.
    * For example, the code
    * {{{
    * Bem("/user-list", "empty" -> true, "/item")
    * }}}
    *
    * would output <code>user-list__item</code> when <code>parentKeepsModifiers</code> is <code>false</code>
    * and <code>user-list--empty__item</code> when <code>parentKeepsModifiers</code> is <code>true</code>.
    *
    * The latter is very rare in the BEM world and therefore the default is <code>false</code>.
    */
  def parentKeepsModifiers: Boolean = false

  /** Whether a child element should inherit the modifiers from its parent block/element.
    * For example, the code
    * {{{
    * val block = Bem("/user-list", "empty" -> true)
    * div(block, "/item")
    * }}}
    * would output the classes <code>user-list__item</code> when <code>childInheritsModifiers</code> is <code>false</code>
    * and <code>user-list__item--empty</code> (together with <code>user-list__item</code>) when <code>childInheritsModifiers</code> is <code>true</code>.
    *
    * Since this allows you to group modifiers for block and elements together, the default is <code>true</code>.
    */
  def childInheritsModifiers: Boolean = true

  /** How the name of a val/def, when using the scala 3 extensions, should be transformed into css class fragments.
    * Take a look at  [[org.felher.beminar.BemValRenameStrategy]] for more information.
    */
  def valRenameStrategy: BemValRenameStrategy = BemValRenameStrategy.kebab

  def withElementSeparator(elementSeparator: String): BemConfig = {
    val _elementSeparator = elementSeparator
    new BemConfig {
      override def elementSeparator       = _elementSeparator
      override def modifierSeparator      = BemConfig.this.modifierSeparator
      override def modifierValueSeparator = BemConfig.this.modifierValueSeparator
      override def parentKeepsModifiers   = BemConfig.this.parentKeepsModifiers
      override def childInheritsModifiers = BemConfig.this.childInheritsModifiers
      override def valRenameStrategy      = BemConfig.this.valRenameStrategy
    }
  }

  def withModifierSeparator(modifierSeparator: String): BemConfig = {
    val _modifierSeparator = modifierSeparator
    new BemConfig {
      override def elementSeparator       = BemConfig.this.elementSeparator
      override def modifierSeparator      = _modifierSeparator
      override def modifierValueSeparator = BemConfig.this.modifierValueSeparator
      override def parentKeepsModifiers   = BemConfig.this.parentKeepsModifiers
      override def childInheritsModifiers = BemConfig.this.childInheritsModifiers
      override def valRenameStrategy      = BemConfig.this.valRenameStrategy
    }
  }

  def withModifierValueSeparator(modifierValueSeparator: String): BemConfig = {
    val _modifierValueSeparator = modifierValueSeparator
    new BemConfig {
      override def elementSeparator       = BemConfig.this.elementSeparator
      override def modifierSeparator      = BemConfig.this.modifierSeparator
      override def modifierValueSeparator = _modifierValueSeparator
      override def parentKeepsModifiers   = BemConfig.this.parentKeepsModifiers
      override def childInheritsModifiers = BemConfig.this.childInheritsModifiers
      override def valRenameStrategy      = BemConfig.this.valRenameStrategy
    }
  }

  def withParentKeepsModifiers(parentKeepsModifiers: Boolean): BemConfig = {
    val _parentKeepsModifiers = parentKeepsModifiers
    new BemConfig {
      override def elementSeparator       = BemConfig.this.elementSeparator
      override def modifierSeparator      = BemConfig.this.modifierSeparator
      override def modifierValueSeparator = BemConfig.this.modifierValueSeparator
      override def parentKeepsModifiers   = _parentKeepsModifiers
      override def childInheritsModifiers = BemConfig.this.childInheritsModifiers
      override def valRenameStrategy      = BemConfig.this.valRenameStrategy
    }
  }

  def withChildInheritsModifiers(childInheritsModifiers: Boolean): BemConfig = {
    val _childInheritsModifiers = childInheritsModifiers
    new BemConfig {
      override def elementSeparator       = BemConfig.this.elementSeparator
      override def modifierSeparator      = BemConfig.this.modifierSeparator
      override def modifierValueSeparator = BemConfig.this.modifierValueSeparator
      override def parentKeepsModifiers   = BemConfig.this.parentKeepsModifiers
      override def childInheritsModifiers = _childInheritsModifiers
      override def valRenameStrategy      = BemConfig.this.valRenameStrategy
    }
  }

  def withValRenameStrategy(valRenameStrategy: BemValRenameStrategy): BemConfig = {
    val _valRenameStrategy = valRenameStrategy
    new BemConfig {
      override def elementSeparator       = BemConfig.this.elementSeparator
      override def modifierSeparator      = BemConfig.this.modifierSeparator
      override def modifierValueSeparator = BemConfig.this.modifierValueSeparator
      override def parentKeepsModifiers   = BemConfig.this.parentKeepsModifiers
      override def childInheritsModifiers = BemConfig.this.childInheritsModifiers
      override def valRenameStrategy      = _valRenameStrategy
    }
  }
}

object BemConfig {
  val default: BemConfig = new BemConfig {}
}
