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
// Add dependency on JavaFX library based on JAVA_HOME variable
unmanagedJars in Compile += Attributed.blank(
  file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar")
)
wartremoverWarnings ++= Warts.unsafe
