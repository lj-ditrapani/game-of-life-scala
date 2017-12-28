package info.ditrapani.gameoflife

import config.Config
import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

class CanvasDimensions(grid: Grid, config: Config) {
  private val cell_width = config.width
  private val cell_margin = config.margin

  def height(): Double = ((cell_width + cell_margin) * grid.height + cell_margin).toDouble
  def width(): Double = ((cell_width + cell_margin) * grid.width + cell_margin).toDouble
}

object Main extends JFXApp with JavaFxApp {
  val terminator = new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl)
  val params = new Params(parameters)
  new Life(this).main(params, terminator)

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def startGfx(grid: Grid, config: Config): Unit = {
    var curr_grid = grid
    val time_delta: Long = config.time_delta * 1000000L
    val boxDrawer = createSceneAndBoxDrawer(grid, config)
    val sceneDrawer = new SceneDrawer(config, boxDrawer)

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

  def createSceneAndBoxDrawer(grid: Grid, config: Config): BoxDrawer = {
    val canvasDimensions = new CanvasDimensions(grid, config)
    val canvas_height = canvasDimensions.height()
    val canvas_width = canvasDimensions.width()
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

    new BoxDrawerImpl(gc)
  }
}
