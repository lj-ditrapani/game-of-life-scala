lazy val root = (project in file(".")).settings(
  name := "game-of-life",
  version := "1.3.0",
  scalaVersion := "2.12.4",
  organization := "ditrapani.info",
  // Fork a new JVM for 'run' and 'test:run', to
  // avoid JavaFX double initialization problems
  fork := true
)

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture"
)

libraryDependencies ++= Seq(
  "io.monix" %% "monix-eval" % "3.0.0-M2",
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.typelevel" %% "cats-core" % "1.0.0-RC2",
// Tests
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
wartremoverWarnings ++= Warts.allBut(
  Wart.Enumeration,
  Wart.Equals,
  Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.Overloading,
  Wart.PublicInference
)

scalafmtVersion in ThisBuild := "1.3.0"
scalafmtOnCompile in ThisBuild := true
