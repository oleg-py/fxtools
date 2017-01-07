lazy val fxtools  = project.
  in (file(".")).
  aggregate (cats, monix, controls).
  dependsOn (cats % withTests, monix % withTests, controls % withTests)

def withTests = "compile->compile;test->test"

lazy val cats     = project.settings(commonSettings)
lazy val monix    = project.settings(commonSettings)
lazy val controls = project.settings(commonSettings)

Project.inThisBuild(Seq(
  version := "0.0.9",
  organization := "com.github.oleg-py",
  isSnapshot := true,
  scalaVersion := "2.11.8"
))

lazy val commonSettings = Seq(
  Seq(
    moduleName ~= { "fxtools-" ++ _ },
    fork := true,

    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-feature",
      "-deprecation",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xlint",
      "-Xfuture"
    ),

    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "8.0.102-R11",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  ),
  changeDirs("src", "resources", Compile),
  changeDirs("test", "fixtures", Test)
).flatten

def changeDirs(sources: String, resources: String, cfg: Configuration) = Seq(
  sourceDirectory   in cfg := baseDirectory.value / sources,
  resourceDirectory in cfg := baseDirectory.value / resources,
  scalaSource       in cfg := (sourceDirectory in cfg).value
)
