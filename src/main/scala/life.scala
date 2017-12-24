package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

object Life extends JFXApp {
  val params = new Params(parameters)
  Config
    .load(params.unnamed, params.named)
    .map(runGame)
    .left
    .foreach(printErrorHelpAndExit)

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

  def runGame(config: Config): Either[String, Unit] =
    Grid.build(config.board_str).map(startGfx(_, config))

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def startGfx(grid: Grid, config: Config): Unit = {
    var curr_grid = grid
    val time_delta: Long = config.time_delta * 1000000L
    val gc = makeGfxContext(grid, config)
    val drawScene = makeSceneDrawer(config, gc)

    drawScene(curr_grid)

    var last_time = System.nanoTime()

    AnimationTimer(curr_time => {
      if (curr_time - last_time > time_delta) {
        last_time = curr_time
        curr_grid = curr_grid.next
        drawScene(curr_grid)
      }
    }).start()
  }

  def makeGfxContext(grid: Grid, config: Config): GraphicsContext = {
    val width = config.width
    val margin = config.margin
    val canvas_height = ((width + margin) * grid.height + margin).toDouble
    val canvas_width = ((width + margin) * grid.width + margin).toDouble
    val canvas = new Canvas(canvas_width, canvas_height)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    val (r, g, b) = config.bg_color
    gc.setFill(Color.rgb(r, g, b))
    gc.fillRect(0, 0, canvas_width, canvas_height)

    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(canvas_width, canvas_height) {
        content = canvas
      }
    }

    gc
  }

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def makeSceneDrawer(config: Config, gc: GraphicsContext): Grid => Unit = {
    def tupleRgb = Function.tupled[Int, Int, Int, Color](Color.rgb)
    val alive_color = tupleRgb(config.alive_color)
    val dead_color = tupleRgb(config.dead_color)
    val margin = config.margin
    val width = config.width

    (grid) =>
      {
        var x = width * -1
        var y = width * -1
        for (row <- grid.cells) {
          x = width * -1
          y += (width + margin)
          for (cell <- row) {
            x += (width + margin)
            val color = if (cell.alive) alive_color else dead_color
            gc.setFill(color)
            gc.fillRect(x.toDouble, y.toDouble, width.toDouble, width.toDouble)
          }
        }
      }
  }
}
