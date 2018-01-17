lazy val root = (project in file(".")).settings(
  name := "game-of-life",
  version := "1.4.1",
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
  "io.monix" %% "monix-eval" % "3.0.0-M3",
  "org.typelevel" %% "cats-free" % "1.0.1",
  "org.mockito" % "mockito-core" % "2.13.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
wartremoverWarnings ++= Warts.allBut(
  Wart.Equals,
  Wart.NonUnitStatements
)

scalafmtVersion in ThisBuild := "1.4.0"
scalafmtOnCompile in ThisBuild := true
