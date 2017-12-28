package info.ditrapani.gameoflife

import config.Config
import scalafx.application.JFXApp
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

object Life extends JFXApp {
  val terminator = new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl)
  val params = new Params(parameters)
  Config
    .parse(params.unnamed, params.named)
    .map(runGame)
    .left
    .foreach(terminator.printErrorHelpAndExit)

  def runGame(config: Config): Either[String, Unit] =
    BoardLoader.getBoardStr(config.board_source).flatMap { board_str =>
      Grid.build(board_str).map(startGfx(_, config))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def startGfx(grid: Grid, config: Config): Unit = {
    var curr_grid = grid
    val time_delta: Long = config.time_delta * 1000000L
    val gc = makeGfxContext(grid, config)
    val sceneDrawer = new SceneDrawer(config, new BoxDrawerImpl(gc))

    sceneDrawer.drawScene(curr_grid)

    var last_time = System.nanoTime()

    AnimationTimer(curr_time => {
      if (curr_time - last_time > time_delta) {
        last_time = curr_time
        curr_grid = curr_grid.next
        sceneDrawer.drawScene(curr_grid)
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
}
