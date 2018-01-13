package info.ditrapani.gameoflife

import config.Config
import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.Scene
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

trait JavaFxApp {
  def createStageAndBoxDrawer(grid: Grid, config: Config): BoxDrawer
}

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

class CanvasDimensions(grid: Grid, config: Config) {
  private val cellWidth = config.width
  private val cellMargin = config.margin

  def height(): Double = ((cellWidth + cellMargin) * grid.height + cellMargin).toDouble
  def width(): Double = ((cellWidth + cellMargin) * grid.width + cellMargin).toDouble
}

object Main extends JFXApp with JavaFxApp {
  new Life(BoardLoaderImpl, this, SceneDrawerFactoryImpl, AnimatorFactoryImpl, StepperFactoryImpl)
    .main(new Params(parameters), new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl))

  def createStageAndBoxDrawer(grid: Grid, config: Config): BoxDrawer = {
    val canvasDimensions = new CanvasDimensions(grid, config)
    val canvasHeight = canvasDimensions.height()
    val canvasWidth = canvasDimensions.width()
    val canvas = new Canvas(canvasWidth, canvasHeight)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    val (r, g, b) = config.bgColor
    gc.setFill(Color.rgb(r, g, b))
    gc.fillRect(0, 0, canvasWidth, canvasHeight)

    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(canvasWidth, canvasHeight) {
        content = canvas
      }
    }

    new BoxDrawerImpl(gc)
  }
}
