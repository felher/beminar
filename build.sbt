lazy val supportedScalaVersions = List("2.13.13", "3.3.3")

ThisBuild / organization         := "org.felher"
ThisBuild / organizationName     := "Felix Herrmann"
ThisBuild / version              := "1.0.0"
ThisBuild / organizationHomepage := Some(url("https://felher.org"))
ThisBuild / scalaVersion         := supportedScalaVersions.head

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/felher/beminar/"),
    "scm:git@github.com:felher/beminar.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "felher",
    name = "Felix Herrmann",
    email = "felix@herrmann-koenigsberg.de",
    url = url("https://felher.org")
  )
)

ThisBuild / description       := "A simple BEM utility class for Laminar"
ThisBuild / licenses          := List(sbt.librarymanagement.License.MIT)
ThisBuild / homepage          := Some(url("https://github.com/felher/beminar/"))
ThisBuild / publishTo         := sonatypePublishToBundle.value
ThisBuild / publishMavenStyle := true

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name                   := "beminar",
    crossScalaVersions     := supportedScalaVersions,
    usePgpKeyHex("DE132E3B66E5239F490F52AB3DA07E9E7CFDB415"),
    Test / publishArtifact := true,
    mimaPreviousArtifacts  := Set("org.felher" %%% "beminar" % "1.0.0"),
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        Seq(
          "-language:strictEquality",
          "-feature",
          "-deprecation",
          "-Ykind-projector:underscores",
          "-Ysafe-init",
          "-Xmax-inlines:256",
          "-Wunused:all",
          "-Wvalue-discard",
          "-Wconf:any:verbose"
        )
      case _            =>
        Seq(
          "-Xsource:3-cross",
          "-encoding",
          "utf8",
          "-deprecation",
          "-feature",
          "-unchecked",
          "-language:experimental.macros",
          "-language:higherKinds",
          "-language:implicitConversions",
          "-Wvalue-discard",
          "-Wunused:implicits",
          "-Wunused:explicits",
          "-Wunused:imports",
          "-Wunused:locals",
          "-Wunused:params",
          "-Wunused:privates"
        )
    }),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo"    %%% "laminar"     % "17.1.0" % Provided,
      "com.lihaoyi"  %%% "utest"       % "0.8.2"  % Test
    ),
    jsEnv                  := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

lazy val testMatrix = project
  .in(file("test-matrix"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo"   %%% "laminar" % "17.1.0",
      "org.felher"  %%% "beminar" % "1.0.0",
      "org.felher"  %%% "beminar" % "1.0.0" % Test classifier "tests",
      "com.lihaoyi" %%% "utest"   % "0.8.2" % Test
    ),
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    commands += Command.command("testLaminarCompatibility") { state =>
      final case class CompatTestKey(
          scalaVersion: String,
          laminarVersion: String,
          beminarVersion: String
      )

      val laminarVersions = List("0.14.5", "15.0.1", "16.0.0", "17.0.0")
      val beminarVersions = List("0.16.0", "1.0.0")
      val scalaVersions   = supportedScalaVersions
      val allTestKeys     = for {
        scalaVersion   <- scalaVersions
        laminarVersion <- laminarVersions
        beminarVersion <- beminarVersions
      } yield CompatTestKey(scalaVersion, laminarVersion, beminarVersion)

      def setVersions(depList: Seq[ModuleID], laminarVersion: String, beminarVersion: String): Seq[ModuleID] =
        depList.map(dep => {
          if (dep.organization == "com.raquo" && dep.name == "laminar") {
            dep.withRevision(laminarVersion)
          } else if (dep.organization == "org.felher" && dep.name == "beminar") {
            dep.withRevision(beminarVersion)
          } else {
            dep
          }
        })

      val results = allTestKeys.foldLeft(Map.empty[CompatTestKey, Boolean])((results, testKey) => {
        // this code is tricky. The scalajs plugin changes the dependency list depending on the scala version.
        // So just adding `scalaVersion := "3.3.3"` to the `newSettings`
        // list doesn't work, because the old dependency list is out of date.
        //
        // Instead of adding a setting, we set the new scala using the `set` command, which seems
        // to trigger scalajs updating the dependency list.
        val withNewScala = Command.process("set scalaVersion := \"" + testKey.scalaVersion + "\"", state)
        val oldDepList   = Project.extract(withNewScala).get(libraryDependencies)
        val newDepList   = setVersions(oldDepList, testKey.laminarVersion, testKey.beminarVersion)
        val newSettings  = Seq(libraryDependencies := newDepList)
        val testState    = Project.extract(withNewScala).appendWithSession(newSettings, withNewScala)
        val testsOk      = Project.runTask(Test / test, testState).fold(false)(_._2.toEither.isRight)
        results + (testKey -> testsOk)
      })

      val header  = "||" + beminarVersions.map("Beminar " + _).mkString("|") + "|"
      val sepator = "|" + List.fill(beminarVersions.size + 1)("-").mkString("|") + "|"

      def makeCell(laminarVersion: String, beminarVersion: String): String = {
        val passed = results.filter(entry =>
          entry._1.laminarVersion == laminarVersion && entry._1.beminarVersion == beminarVersion && entry._2
        )
        if (passed.isEmpty) {
          "❌"
        } else {
          "scala " + passed.keys.map(_.scalaVersion).toList.sorted.map(_.replaceAll("\\.\\d+$", "")).mkString(", ")
        }
      }
      def makeRow(laminarVersion: String): String                          = {
        val cells = beminarVersions.map(makeCell(laminarVersion, _))
        s"| Laminar $laminarVersion | ${cells.mkString("|")} |"
      }

      println(header)
      println(sepator)
      laminarVersions.foreach(laminarVersion => println(makeRow(laminarVersion)))

      state
    }
  )
