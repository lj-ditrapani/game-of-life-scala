lazy val root = (project in file(".")).settings(
  name := "game-of-life",
  version := "1.4.2",
  scalaVersion := "2.12.8",
  organization := "info.ditrapani",
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
  "io.monix" %% "monix-eval" % "3.0.0-RC2",
  "org.typelevel" %% "cats-free" % "1.5.0",
  "org.mockito" % "mockito-core" % "2.23.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
wartremoverWarnings ++= Warts.allBut(
  Wart.Equals,
  Wart.NonUnitStatements
)

scalafmtOnCompile in ThisBuild := true
