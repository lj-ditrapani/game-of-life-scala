lazy val root = (project in file(".")).
  settings(
    name := "Game of Life",
    version := "0.1",
    scalaVersion := "2.11.8",
    // Fork a new JVM for 'run' and 'test:run', to
    // avoid JavaFX double initialization problems
    fork := true
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9"
libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
wartremoverWarnings ++= Warts.unsafe
