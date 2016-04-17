package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer

object LifeFX extends JFXApp {
  val boards = Vector(
    "acorn",
    "blinkers",
    "blinker",
    "diehard",
    "glider",
    "pentadecathlon",
    "r-pentomino"
  )

  println("command line args")
  println("unnamed: " + parameters.unnamed.mkString(" | "))
  println("named  : " + parameters.named.mkString(" | "))
  if (parameters.unnamed.exists(p => p == "--help")) {
    printHelpAndExit()
  } else if (parameters.named.isEmpty) {
    printHelpAndExit()
  } else {
    // val time_delta = parameters.named.getOrElse("t", "500").toInt
    val (flag, value) = parameters.named.head
    flag match {
      case "b" | "built-in" => loadBuiltIn(value)
      case "f" | "file" => loadExternalFile(value)
      case _ => printHelpAndExit()
    }
  }

  def printHelpAndExit(): Unit = {
    println(
      """Usage:
        |java -jar game-of-life-assembly-0.1.0.jar --b=<n>    |
        |                                          --f=<file> |
        |                                          --help
        |                                          [--t=<n>]
        |
        |--b=<n>    Run a built-in board where n is a number that maps to the
        |           corresponding game board as defined in the table below.
        |
        |--f=<file> Run a board defined in a file on disk.  The board must be
        |           at least 3X3.  It can contain only +, - and newlines.  Each
        |           line represents a row. + means alive and - means dead.  All
        |           lines must be equal length.
        |
        |--help     Print this help text and exit
        |
        |--t=<n>    Optional argument.  The time delta between simulation steps
        |           in miliseconds.  Smaller n means faster simulation.
        |           Defaults to 500 ms.
        |
        |
        |Built-in game boards
        |--------------------""".stripMargin)
    for ((name, index) <- boards.view.zipWithIndex) {
      println(s"    ${index + 1}  $name")
    }
    println("\n")
    println(
      """Example game board:
        |------
        |---+--
        |---+--
        |---+--
        |------""".stripMargin
    )
    System.exit(0)
  }

  def loadBuiltIn(num_str: String): Unit = {
    val n = try {
      loadBuiltIn(num_str.toInt)
    } catch {
      case e: NumberFormatException => printHelpAndExit()
    }
  }

  def loadBuiltIn(n: Int): Unit = {
    if (n < 1 || n > boards.size) {
      printHelpAndExit()
    }
    val name = boards(n - 1)
    val input_stream = getClass.getResourceAsStream(s"/$name.txt")
    val board_str = scala.io.Source.fromInputStream(input_stream).mkString
    Grid.build(board_str).right.map(startGfx(_))
  }

  def loadExternalFile(file_name: String): Unit = {
  }

  def startGfx(grid: Grid): Unit = {
    val margin = 4
    val width = 16
    val canvas_height = (width + margin) * grid.height + margin
    val canvas_width = (width + margin) * grid.width + margin
    val canvas = new Canvas(canvas_width, canvas_height)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    gc.setFill(Color.rgb(20, 20, 20))
    gc.fillRect(0, 0, canvas_width, canvas_height)

    val alive_color = Color.rgb(200, 220, 255)
    val dead_color = Color.rgb(100, 120, 155)

    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(canvas_width, canvas_height) {
        content = canvas
      }
    }

    var last_time = System.nanoTime()
    var curr_grid = grid

    def drawScene(): Unit = {
      var x = width * -1
      var y = width * -1
      for (row <- curr_grid.cells) {
        x = width * -1
        y += (width + margin)
        for (cell <- row) {
          x += (width + margin)
          val color = if (cell.alive) alive_color else dead_color
          gc.setFill(color)
          gc.fillRect(x, y, width, width)
        }
      }
    }

    drawScene()

    AnimationTimer(curr_time => {
      if (curr_time - last_time > 500000000) {
        last_time = curr_time
        curr_grid = curr_grid.next
        drawScene()
      }
    }).start()
  }
}
