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


Dev
---

Generate a standalone jar for java 8

    sbt assembly

The jar will be placed in target/scala-x.xx/game-of-life-assembly-x.x.x.jar

Note:  If using openjdk-8-jdk on Ubuntu/Debian, you need to additionally
install the openjfx package to get the javaFX runtime.

Style check; I'm using both wartremover and scalastyle.  The compile task also runs wartremover.

    sbt test            # or anything that runs compile
    sbt scalastyle


TODO
----

In config specs; make a `val blinker_board_num = "3"`
or `val blinker_board_num = boards.get_index("blinker").toString`
to easily change in case list order changes when adding new boards

Refactor Life:
- Maybe make parameterized a setup stage method that does the one-time setup
- Maybe make drawScene a separate parameterized method, to shorten the startGfx method
- bump version to 1.1.0

Refactor Config implementation

Refactor Config specs
