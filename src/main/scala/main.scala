package info.ditrapani.gameoflife

import config.{Config, Error, Go, Help}
import java.util.concurrent.atomic.AtomicReference
import monix.execution.Scheduler.Implicits.global
import javafx.scene.paint.Color
import terminator.Terminator

class CanvasConfig(grid: Grid, config: Config) {
  private val cellWidth = config.width
  private val cellMargin = config.margin

  def height(): Double = ((cellWidth + cellMargin) * grid.height + cellMargin).toDouble
  def width(): Double = ((cellWidth + cellMargin) * grid.width + cellMargin).toDouble
  def color(): Color = Function.tupled[Int, Int, Int, Color](Color.rgb)(config.bgColor)
}

class Main(
    boardLoader: BoardLoader,
    javaFxInit: JavaFxInit,
    sceneDrawerFactory: SceneDrawerFactory,
    animatorFactory: AnimatorFactory,
    stepperFactory: StepperFactory
) {

  def main(params: Params, terminator: Terminator): Unit =
    Config.parse(params.unnamed, params.named) match {
      case Help => terminator.help()
      case Error(s) => terminator.error(s)
      case Go(config) =>
        runGame(config) match {
          case Left(s) => terminator.error(s)
          case Right(_) => (): Unit
        }
    }

  def runGame(config: Config): Either[String, Unit] =
    boardLoader.getBoardStr(config.boardSource).flatMap { boardStr =>
      Grid.build(boardStr).map(startGfx(_, config))
    }

  def startGfx(grid: Grid, config: Config): Unit = {
    val canvasConfig = new CanvasConfig(grid, config)
    val width = canvasConfig.width()
    val height = canvasConfig.height()
    val color = canvasConfig.color()
    val boxDrawer = javaFxInit.startApp(width, height, color)
    val sceneDrawer = sceneDrawerFactory(config, boxDrawer)
    val gridRef = new AtomicReference[Option[Grid]](Some(grid))
    stepperFactory(gridRef, config.timeDelta).run(grid, Infinity).runAsync
    animatorFactory(gridRef, sceneDrawer).run()
    (): Unit
  }
}
