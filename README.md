Conway's Game of Life on 2-D toroidal grid in Scala & ScalaFX.


Download Standalone jar
-----------------------

<http://ditrapani.info/resources/game-of-life-assembly-1.1.0.jar>


Usage
-----

Example

    java -jar game-of-life-assembly-x.x.x.jar --b=1

For more details and list of built-in game boards:

    java -jar game-of-life-assembly-x.x.x.jar --help

See src/main/resources/help.txt for documentation.


Dependencies
------------

Requires Java 8 and javaFX.
Java 8 distributions from Oracle include the javaFX runtime.

If using openjdk-8-jdk on linux, you may need to additionally install
the openjfx package to get the javaFX runtime.  For example,

Ubuntu/Debian

    sudo apt-get install openjfx

Arch

    sudo pacman -S java-openjfx


Dev
---

Generate a standalone jar for java 8

    sbt assembly

The jar will be placed in target/scala-x.xx/game-of-life-assembly-x.x.x.jar

Style check; I'm using both wartremover and scalastyle.  The compile task also runs wartremover.

    sbt test            # or anything that runs compile
    sbt scalastyle


TODO
----

Make some games boards larger now that you can control margin & width

Add more boards

Add --alive-color & --dead-color to Config?

Refactor Config implementation

Refactor Config specs
