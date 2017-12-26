Conway's Game of Life on 2-D toroidal grid in Scala & ScalaFX.


Download Standalone jar
-----------------------

<http://ditrapani.info/resources/game-of-life-assembly-1.2.0.jar>


Usage
-----

Example

    java -jar game-of-life-assembly-x.x.x.jar --b=2

For more details and list of built-in game boards:

    java -jar game-of-life-assembly-x.x.x.jar --help

See
[src/main/resources/help.txt](https://github.com/lj-ditrapani/game-of-life-scala/blob/master/src/main/resources/help.txt)
for documentation.


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

Style check; I'm using both wartremover and scalastyle.
The compile task also runs wartremover.

    sbt test            # or anything that runs compile
    sbt scalastyle

Test coverage

    sbt clean coverage test
    sbt coverageReport

Check for dependency updates

    sbt dependencyUpdates


TODO
----

- Move board_str out of Config class;  then load can be pure.
    - File loading (built-in or file) can be done in IO monad
- Get rid of animation timer and use recursive function with Talk.delay instead
    - impossible to overflow; no need to throttle
    - guarantees only one thread writing to gfx canvas at a time
    - new grid gets passed to next recursive call; no need for mutable var or MVar
- Use nested recursive function (yloop and xloop) for drawScene
    - no more vars
    - Task only for gc.setColor & gc.fillRect
    - abstract gc to DI interface (makes it testable)
