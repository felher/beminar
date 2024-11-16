package org.felher.beminar

import utest.*
import org.scalajs.dom

import org.scalajs.dom.document

import com.raquo.laminar.api.L.*

object BemSpec extends TestSuite {

  def tests = Tests {
    //@formatter:off
    test("Empty Fragments don't generate any classes") - checkMultiSep("")

    test("1 / string generates a block")            - checkMultiSep("block",                     "block")
    test("2 / strings generate block and element")  - checkMultiSep("block__element", "/block",  "/element")
    test("3 / strings generate block and elements") - checkMultiSep("block__element1__element2", "/block", "/element1", "/element2")

    test("implicit framgent string")  - checkMultiSep("block block--modifier", "/block", "modifier")
    test("implicit framgent stringS") - checkMultiSep("block block--modifier", "/block", Signal.fromValue("modifier"))

    test("implicit framgent boolean tuple true ")   - checkMultiSep("block block--modifier", "/block", "modifier" -> true)
    test("implicit framgent boolean tuple true S")  - checkMultiSep("block block--modifier", "/block", "modifier" -> Signal.fromValue(true))
    test("implicit framgent boolean tuple true SS") - checkMultiSep("block block--modifier", "/block", Signal.fromValue("modifier" -> true))

    test("implicit framgent boolean tuple false ")   - checkMultiSep("block", "/block", "modifier" -> false)
    test("implicit framgent boolean tuple false S")  - checkMultiSep("block", "/block", "modifier" -> Signal.fromValue(false))
    test("implicit framgent boolean tuple false SS") - checkMultiSep("block", "/block", Signal.fromValue("modifier" -> false))

    test("implicit framgent string tuple ")   - checkMultiSep("block block--modifier_value", "/block", "modifier" -> "value")
    test("implicit framgent string tuple S")  - checkMultiSep("block block--modifier_value", "/block", "modifier" -> Signal.fromValue("value"))
    test("implicit framgent string tuple SS") - checkMultiSep("block block--modifier_value", "/block", Signal.fromValue("modifier" -> "value"))

    test("implicit framgent int tuple ")   - checkMultiSep("block block--level_1", "/block", "level" -> 1)
    test("implicit framgent int tuple S")  - checkMultiSep("block block--level_1", "/block", "level" -> Signal.fromValue(1))
    test("implicit framgent int tuple SS") - checkMultiSep("block block--level_1", "/block", Signal.fromValue("level" -> 1))

    test("implicit framgent string map ")   - checkMultiSep("block block--m1_v1 block--m2_v2", "/block", Map("m1" -> "v1", "m2" -> "v2"))
    test("implicit framgent string map S")  - checkMultiSep("block block--m1_v1 block--m2_v2", "/block", Map("m1" -> Signal.fromValue("v1"), "m2" -> Signal.fromValue("v2")))
    test("implicit framgent string map SS") - checkMultiSep("block block--m1_v1 block--m2_v2", "/block", Signal.fromValue(Map("m1" -> "v1", "m2" -> "v2")))

    test("implicit framgent boolean map ")   - checkMultiSep("block block--m1 block--m3", "/block", Map("m1" -> true, "m2" -> false, "m3" -> true))
    test("implicit framgent boolean map S")  - checkMultiSep("block block--m1 block--m3", "/block", Map("m1" -> Signal.fromValue(true), "m2" -> Signal.fromValue(false), "m3" -> Signal.fromValue(true)))
    test("implicit framgent boolean map SS") - checkMultiSep("block block--m1 block--m3", "/block", Signal.fromValue(Map("m1" -> true, "m2" -> false, "m3" -> true)))

    test("implicit framgent int map ")   - checkMultiSep("block block--m1_1 block--m2_2", "/block", Map("m1" -> 1, "m2" -> 2))
    test("implicit framgent int map S")  - checkMultiSep("block block--m1_1 block--m2_2", "/block", Map("m1" -> Signal.fromValue(1), "m2" -> Signal.fromValue(2)))
    test("implicit framgent int map SS") - checkMultiSep("block block--m1_1 block--m2_2", "/block", Signal.fromValue(Map("m1" -> 1, "m2" -> 2)))

    test("you should be able to add modfiers multiple times 1") - checkSingleBem("",                                                 Bem())
    test("you should be able to add modfiers multiple times 2") - checkSingleBem("b",                                                Bem()("/b"))
    test("you should be able to add modfiers multiple times 3") - checkSingleBem("b__e",                                             Bem()("/b")("/e"))
    test("you should be able to add modfiers multiple times 4") - checkSingleBem("b__e b__e--m",                                     Bem()("/b")("/e")("m"))
    test("you should be able to add modfiers multiple times 5") - checkSingleBem("b__e b__e--m b__e--m-v",                           Bem()("/b")("/e")("m")("m" -> "v"))
    test("you should be able to add modfiers multiple times 6") - checkSingleBem("b__e b__e--m b__e--m-v",                           Bem()("/b")("/e")("m")("m" -> "v")("mf" -> false))
    test("you should be able to add modfiers multiple times 7") - checkSingleBem("b__e b__e--m b__e--m-v",                           Bem()("/b")("/e")("m")("m" -> "v")("mf" -> false)("mf" -> false)("mf" -> false)("mf" -> false)("mf" -> false))
    test("you should be able to add modfiers multiple times 8") - checkSingleBem("b__e b__e--m b__e--a-b b__e--c-d",                 Bem()("/b")("/e")("m")(Map("a" -> "b", "c" -> "d")))
    test("you should be able to add modfiers multiple times 9") - checkSingleBem("b__e b__e--m b__e--a-b b__e--c-d b__e--x b__e--z", Bem()("/b")("/e")("m")(Map("a" -> "b", "c" -> "d"))(Map("x" ->true, "y" -> false, "z" -> true)))

    test("common example 1") - checkMultiSep("alert alert--visible alert--serverity_4", "/alert", "visible" -> Signal.fromValue(true), "serverity" -> Signal.fromValue("4"))
    test("common example 2") - checkMultiSep("persons__person persons__person--friend",  "/persons", "/person", "friend")
    test("common example 3") - checkMultiSep("button button--primary button--disabled", "/button", "primary" -> true, "disabled" -> true)

    test("no-keep no-inherit should only keep the last modifers")                      - checkSingleBem("b1__b2 b1__b2--b2m1 b1__b2--b2m2",                            Bem("c0m1", "c0m2", "/b1", "b1m1", "b1m2", "/b2", "b2m1", "b2m2").modifyConfig(_.withParentKeepsModifiers(false).withChildInheritsModifiers(false)))
    test("no-keep do-inherit should accumulate the modifers")                          - checkSingleBem("b1__b2 b1__b2--c0m1 b1__b2--b1m1 b1__b2--b2m1",               Bem("c0m1", "/b1", "b1m1", "/b2", "b2m1").modifyConfig(_.withParentKeepsModifiers(false).withChildInheritsModifiers(true)))
    test("do-keep no-inherit should generate all classes with modifers and keep them") - checkSingleBem("b1 c0m1__b1 b1--b1m1 c0m1__b1--b1m1",                         Bem("c0m1", "/b1", "b1m1").modifyConfig(_.withParentKeepsModifiers(true).withChildInheritsModifiers(false)))
    test("do-keep do-inherit should do both")                                          - checkSingleBem("b1 c0m1__b1 b1--c0m1 b1--b1m1 c0m1__b1--c0m1 c0m1__b1--b1m1", Bem("c0m1", "/b1", "b1m1").modifyConfig(_.withParentKeepsModifiers(true).withChildInheritsModifiers(true)))

    test("no-keep no-inherit complex") - checkSingleBem(complexExpected(0), complex.modifyConfig(_.withParentKeepsModifiers(false).withChildInheritsModifiers(false)))
    test("no-keep do-inherit complex") - checkSingleBem(complexExpected(1), complex.modifyConfig(_.withParentKeepsModifiers(false).withChildInheritsModifiers(true)))
    test("do-keep no-inherit complex") - checkSingleBem(complexExpected(2), complex.modifyConfig(_.withParentKeepsModifiers(true).withChildInheritsModifiers(false)))
    test("do-keep do-inherit complex") - checkSingleBem(complexExpected(3), complex.modifyConfig(_.withParentKeepsModifiers(true).withChildInheritsModifiers(true)))

    test("should also work with vars, not only signals") - checkMultiSep("block block--m1 block--m3", "/block", Var(Map("m1" -> true, "m2" -> false, "m3" -> true)))

    test("macro expansion name rewrite - keep as is") - checkWithConfig(BemConfig.default.withValRenameStrategy(BemValRenameStrategy.asIs),  "block block--isEmpty",  "/block", new BemFragment.BemFragmentMacro(new BemFragment.BemFragmentBooleanMod("isEmpty", true)))
    test("macro expansion name rewrite - to kebab")   - checkWithConfig(BemConfig.default.withValRenameStrategy(BemValRenameStrategy.kebab), "block block--is-empty", "/block", new BemFragment.BemFragmentMacro(new BemFragment.BemFragmentBooleanMod("isEmpty", true)))
    test("macro expansion name rewrite - to snake")   - checkWithConfig(BemConfig.default.withValRenameStrategy(BemValRenameStrategy.snake), "block block--is_empty", "/block", new BemFragment.BemFragmentMacro(new BemFragment.BemFragmentBooleanMod("isEmpty", true)))

    //@formatter:on
  }

  private def checkMultiSep(classString: String, fragments: BemFragment*): Unit = {
    val c1: BemConfig        = BemConfig.default.withModifierValueSeparator("_")
    val f1: String => String = s => s
    val c2: BemConfig        = c1.withElementSeparator("~~").withModifierSeparator("==").withModifierValueSeparator("++")
    val f2: String => String = _.replace("__", "~~").replace("--", "==").replace("_", "++")

    checkWithConfig(c1, f1(classString), fragments: _*)
    checkWithConfig(c2, f2(classString), fragments: _*)
  }

  private def checkWithConfig(config: BemConfig, classString: String, fragments: BemFragment*): Unit = {
    val bem = Bem(fragments: _*).withConfig(config)
    checkSingleBem(classString, bem)
  }

  private def checkSingleBem(classString: String, bem: Bem): Unit = {
    val newDiv = document.createElement("div")
    document.body.appendChild(newDiv)

    render(newDiv, div(bem, "Hello world!"))
    val generatedClasses = newDiv.childNodes(0).asInstanceOf[dom.HTMLElement].classList.toList
    document.body.removeChild(newDiv)
    val expected         = if (classString.isEmpty) List() else classString.split(" ").toList
    assert(generatedClasses == expected)
  }

  private def complexKeepNoInheritList: List[String] = """
    b1__b2
    c0m1__b1__b2
    c0m2__b1__b2
    b1--b1m1__b2
    b1--b1m2__b2
    c0m1__b1--b1m1__b2
    c0m1__b1--b1m2__b2
    c0m2__b1--b1m1__b2
    c0m2__b1--b1m2__b2
    b1__b2--b2m1
    b1__b2--b2m2
    c0m1__b1__b2--b2m1
    c0m1__b1__b2--b2m2
    c0m2__b1__b2--b2m1
    c0m2__b1__b2--b2m2
    b1--b1m1__b2--b2m1
    b1--b1m1__b2--b2m2
    b1--b1m2__b2--b2m1
    b1--b1m2__b2--b2m2
    c0m1__b1--b1m1__b2--b2m1
    c0m1__b1--b1m1__b2--b2m2
    c0m1__b1--b1m2__b2--b2m1
    c0m1__b1--b1m2__b2--b2m2
    c0m2__b1--b1m1__b2--b2m1
    c0m2__b1--b1m1__b2--b2m2
    c0m2__b1--b1m2__b2--b2m1
    c0m2__b1--b1m2__b2--b2m2
    """.linesIterator.toList.map(_.trim).filter(_.nonEmpty)

  private def complexKeepAndInheritList: List[String] = """
      b1__b2
      c0m1__b1__b2
      c0m2__b1__b2
      b1--c0m1__b2
      b1--c0m2__b2
      b1--b1m1__b2
      b1--b1m2__b2
      c0m1__b1--c0m1__b2
      c0m1__b1--c0m2__b2
      c0m1__b1--b1m1__b2
      c0m1__b1--b1m2__b2
      c0m2__b1--c0m1__b2
      c0m2__b1--c0m2__b2
      c0m2__b1--b1m1__b2
      c0m2__b1--b1m2__b2
      b1__b2--c0m1
      b1__b2--c0m2
      b1__b2--b1m1
      b1__b2--b1m2
      b1__b2--b2m1
      b1__b2--b2m2
      c0m1__b1__b2--c0m1
      c0m1__b1__b2--c0m2
      c0m1__b1__b2--b1m1
      c0m1__b1__b2--b1m2
      c0m1__b1__b2--b2m1
      c0m1__b1__b2--b2m2
      c0m2__b1__b2--c0m1
      c0m2__b1__b2--c0m2
      c0m2__b1__b2--b1m1
      c0m2__b1__b2--b1m2
      c0m2__b1__b2--b2m1
      c0m2__b1__b2--b2m2
      b1--c0m1__b2--c0m1
      b1--c0m1__b2--c0m2
      b1--c0m1__b2--b1m1
      b1--c0m1__b2--b1m2
      b1--c0m1__b2--b2m1
      b1--c0m1__b2--b2m2
      b1--c0m2__b2--c0m1
      b1--c0m2__b2--c0m2
      b1--c0m2__b2--b1m1
      b1--c0m2__b2--b1m2
      b1--c0m2__b2--b2m1
      b1--c0m2__b2--b2m2
      b1--b1m1__b2--c0m1
      b1--b1m1__b2--c0m2
      b1--b1m1__b2--b1m1
      b1--b1m1__b2--b1m2
      b1--b1m1__b2--b2m1
      b1--b1m1__b2--b2m2
      b1--b1m2__b2--c0m1
      b1--b1m2__b2--c0m2
      b1--b1m2__b2--b1m1
      b1--b1m2__b2--b1m2
      b1--b1m2__b2--b2m1
      b1--b1m2__b2--b2m2
      c0m1__b1--c0m1__b2--c0m1
      c0m1__b1--c0m1__b2--c0m2
      c0m1__b1--c0m1__b2--b1m1
      c0m1__b1--c0m1__b2--b1m2
      c0m1__b1--c0m1__b2--b2m1
      c0m1__b1--c0m1__b2--b2m2
      c0m1__b1--c0m2__b2--c0m1
      c0m1__b1--c0m2__b2--c0m2
      c0m1__b1--c0m2__b2--b1m1
      c0m1__b1--c0m2__b2--b1m2
      c0m1__b1--c0m2__b2--b2m1
      c0m1__b1--c0m2__b2--b2m2
      c0m1__b1--b1m1__b2--c0m1
      c0m1__b1--b1m1__b2--c0m2
      c0m1__b1--b1m1__b2--b1m1
      c0m1__b1--b1m1__b2--b1m2
      c0m1__b1--b1m1__b2--b2m1
      c0m1__b1--b1m1__b2--b2m2
      c0m1__b1--b1m2__b2--c0m1
      c0m1__b1--b1m2__b2--c0m2
      c0m1__b1--b1m2__b2--b1m1
      c0m1__b1--b1m2__b2--b1m2
      c0m1__b1--b1m2__b2--b2m1
      c0m1__b1--b1m2__b2--b2m2
      c0m2__b1--c0m1__b2--c0m1
      c0m2__b1--c0m1__b2--c0m2
      c0m2__b1--c0m1__b2--b1m1
      c0m2__b1--c0m1__b2--b1m2
      c0m2__b1--c0m1__b2--b2m1
      c0m2__b1--c0m1__b2--b2m2
      c0m2__b1--c0m2__b2--c0m1
      c0m2__b1--c0m2__b2--c0m2
      c0m2__b1--c0m2__b2--b1m1
      c0m2__b1--c0m2__b2--b1m2
      c0m2__b1--c0m2__b2--b2m1
      c0m2__b1--c0m2__b2--b2m2
      c0m2__b1--b1m1__b2--c0m1
      c0m2__b1--b1m1__b2--c0m2
      c0m2__b1--b1m1__b2--b1m1
      c0m2__b1--b1m1__b2--b1m2
      c0m2__b1--b1m1__b2--b2m1
      c0m2__b1--b1m1__b2--b2m2
      c0m2__b1--b1m2__b2--c0m1
      c0m2__b1--b1m2__b2--c0m2
      c0m2__b1--b1m2__b2--b1m1
      c0m2__b1--b1m2__b2--b1m2
      c0m2__b1--b1m2__b2--b2m1
      c0m2__b1--b1m2__b2--b2m2
      """.linesIterator.toList.map(_.trim).filter(_.nonEmpty)

  def complex         = Bem("c0m1", "c0m2" -> true, "/b1", "b1m1", "b1m2" -> true, "/b2", "b2m1", "b2m2" -> true)
  def complexExpected = List(
    "b1__b2 b1__b2--b2m1 b1__b2--b2m2",
    "b1__b2 b1__b2--c0m1 b1__b2--c0m2 b1__b2--b1m1 b1__b2--b1m2 b1__b2--b2m1 b1__b2--b2m2",
    complexKeepNoInheritList.mkString(" "),
    complexKeepAndInheritList.mkString(" ")
  )
}
