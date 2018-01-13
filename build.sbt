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
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture"
)

libraryDependencies ++= Seq(
  "io.monix" %% "monix-eval" % "2.3.2",
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.mockito" % "mockito-core" % "2.13.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
wartremoverWarnings ++= Warts.allBut(
  Wart.Equals,
  Wart.NonUnitStatements
)

scalafmtVersion in ThisBuild := "1.3.0"
scalafmtOnCompile in ThisBuild := true
