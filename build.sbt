lazy val root = (project in file(".")).
  settings(
    name := "game-of-life",
    version := "1.2.0",
    scalaVersion := "2.11.8",
    organization := "ditrapani.info",
    // Fork a new JVM for 'run' and 'test:run', to
    // avoid JavaFX double initialization problems
    fork := true
  )

scalacOptions += "-target:jvm-1.8"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
wartremoverWarnings ++= Warts.unsafe
