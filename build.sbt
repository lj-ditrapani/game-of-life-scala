lazy val root = (project in file(".")).
  settings(
    name := "Game of Life",
    version := "0.1",
    scalaVersion := "2.11.8"
  )

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
wartremoverWarnings ++= Warts.unsafe
