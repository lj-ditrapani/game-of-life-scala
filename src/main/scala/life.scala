package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import scala.util.{Try, Success, Failure}

object LifeFX extends JFXApp {
  val boards = Vector(
    "acorn",
    "blinkers",
    "blinker",
    "block-laying-switch-engine2",
    "diehard",
    "glider",
    "gosper-glider-gun",
    "pentadecathlon",
    "r-pentomino"
  )

  val params = parameters.named

  if (
    Vector[Boolean](
      parameters.unnamed.exists(p => p == "--help"),
      !parameters.unnamed.isEmpty,
      params.isEmpty,
      params.size > 2,
      params.size == 2 && !params.contains("t"),
      !params.contains("b") && !params.contains("f")
    ).exists(x => x)
  ) {
    printHelpAndExit()
  }

  val time_delta: Long = Try(params.getOrElse("t", "500").toLong)
    .getOrElse(500L) * 1000000L
  val (flag, value) = params.filterKeys(_ != "t").head
  flag match {
    case "b" | "built-in" => loadBuiltIn(value)
    case "f" | "file" => loadExternalFile(value)
    case _ => printHelpAndExit()
  }

  def printHelpAndExit(): Unit = {
    val input_stream = getClass.getResourceAsStream("/help.txt")
    val help_text = scala.io.Source.fromInputStream(input_stream).mkString
    println(help_text)
    for ((name, index) <- boards.zipWithIndex) {
      println(s"    ${index + 1}  $name")
    }
    println("\n")
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
    def printErrorHelpAndExit(message: String): Unit = {
      println(s"\n[ERROR] $message\n")
      printHelpAndExit()
    }
    Try(scala.io.Source.fromFile(file_name).mkString) match {
      case Failure(exception) =>
        printErrorHelpAndExit(exception.toString())
      case Success(board_str) => {
        val board_str = scala.io.Source.fromFile(file_name).mkString
        Grid.build(board_str).fold(printErrorHelpAndExit(_), startGfx(_))
      }
    }
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
      if (curr_time - last_time > time_delta) {
        last_time = curr_time
        curr_grid = curr_grid.next
        drawScene()
      }
    }).start()
  }
}
