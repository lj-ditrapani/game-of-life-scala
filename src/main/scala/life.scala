package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import scala.util.{Try, Success, Failure}

object LifeFX extends JFXApp {

  Config.load(parameters.unnamed, Map(parameters.named.toSeq: _*)) match {
    case Left(s) => printErrorHelpAndExit(s)
    case Right(config) => loadAndRun(config)
  }

  def printErrorHelpAndExit(message: String): Unit = {
    if (message != "Printing help text...") {
      println(s"\n[ERROR] $message\n")
    }
    val input_stream = getClass.getResourceAsStream("/help.txt")
    val help_text = scala.io.Source.fromInputStream(input_stream).mkString
    println(help_text)
    for ((name, index) <- Config.boards.zipWithIndex) {
      println(s"    ${index + 1}  $name")
    }
    println("\n")
    System.exit(0)
  }

  def loadAndRun(config: Config): Unit = {
    Grid.build(config.board_str) match {
      case Left(s) => printErrorHelpAndExit(s)
      case Right(grid) => startGfx(grid, config)
    }
  }

  def startGfx(grid: Grid, config: Config): Unit = {
    var curr_grid = grid
    val time_delta: Long = config.time_delta * 1000000L
    val f: (Int, Int, Int) => Color = Color.rgb _
    def tupleRgb = Function.tupled(f)
    val alive_color = tupleRgb(config.alive_color)
    val dead_color = tupleRgb(config.dead_color)
    val margin = config.margin
    val width = config.width
    val canvas_height = (width + margin) * grid.height + margin
    val canvas_width = (width + margin) * grid.width + margin
    val canvas = new Canvas(canvas_width, canvas_height)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    gc.setFill(Color.rgb(20, 20, 20))
    gc.fillRect(0, 0, canvas_width, canvas_height)

    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(canvas_width, canvas_height) {
        content = canvas
      }
    }

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

    var last_time = System.nanoTime()

    AnimationTimer(curr_time => {
      if (curr_time - last_time > time_delta) {
        last_time = curr_time
        curr_grid = curr_grid.next
        drawScene()
      }
    }).start()
  }
}
