lazy val root = (project in file(".")).
  settings(
    name := "game-of-life",
    version := "1.2.0",
    scalaVersion := "2.12.2",
    organization := "ditrapani.info",
    // Fork a new JVM for 'run' and 'test:run', to
    // avoid JavaFX double initialization problems
    fork := true
  )

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture"
)

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
wartremoverWarnings ++= Warts.allBut(
  Wart.Enumeration,
  Wart.Equals,
  Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.Overloading,
  Wart.PublicInference
)
