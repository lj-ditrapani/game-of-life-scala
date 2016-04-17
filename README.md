Conway's Game of Life on 2-D toroidal grid in Scala & ScalaFX.


Usage
-----

Example

    java -jar game-of-life-assembly-1.0.0.jar --b=1

For more details and list of built-in game boards:

    java -jar game-of-life-assembly-1.0.0.jar --help

See src/main/resources/help.txt for documentation.


Dev
---

Generate a standalone jar for java 8

    sbt assembly

Note:  If using openjdk-8-jdk on Ubuntu/Debian, you need to additionally
install the openjfx package to get the javaFX runtime.

Style check; I'm using both wartremover and scalastyle.  The compile task also runs wartremover.

    sbt test            # or anything that runs compile
    sbt scalastyle
