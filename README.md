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

- test the untested testable code (Life, AnimatorImpl)
- graphicsContext  methods must be called from the JavaFx thread once the gc is attached to a scene
    - Make all gc calls happen from AnimationTimer.handle.  Grid can be computed on another thread.
    - Use AtomicReference[Option[Grid]] as channel between AnimationTimer (consumer) and Stepper (producer)
    - Grid computed by Stepper(gridRef).step(grid): Task[Unit] non-terminating recursive function
        - only steps if gridRef is empty
        - sets gridRef to next grid
    - AnimationTimer.handle only draws if gridRef is non-empty (consumes grid if gridRef non-empty)
- Consider using 2 mutable grids Array[Array[Cell]] dimOf and a aliveCount Array[Array[Int]]
    - one grid is put in AtomicRef for rending and the other is used to to compute the next grid
    - each frame, they swap
    - First, test performance diff; then implement if worth it
- break up into smaller classes; seperate files
- organize/revisit packages
- remove unused dependencies
- some tests in life-spec maybe move to diff package/file
- get a consistent naming scheme (camel vs snake case)
- animatorFactory can become just an animate function that creates the animationTimer and runs it.
- see ^ if applies to others
