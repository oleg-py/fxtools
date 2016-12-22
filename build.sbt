name := "scalafx-tools"
version := "0.0.1"

scalaVersion := "2.11.8"


sourceDirectory in Compile <<= baseDirectory(_ / "src")
scalaSource in Compile <<= baseDirectory(_ / "src")
javaSource in Compile <<= baseDirectory(_ / "src")

sourceDirectory in Test <<= baseDirectory(_ / "test")
scalaSource in Test     <<= baseDirectory(_ / "test")
javaSource in Test      <<= baseDirectory(_ / "test")

resourceDirectory in Compile <<= baseDirectory(_ / "resources")
resourceDirectory in Test    <<= baseDirectory(_ / "fixtures")


libraryDependencies ++= Seq(
  "io.monix" %% "monix-reactive" % "2.1.2",
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

fork := true // Avoid problems with JavaFX double initalization

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
)
