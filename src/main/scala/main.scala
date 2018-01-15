package info.ditrapani.gameoflife

import config.{Config, Error, Go, Help}
import effects.Effects.{
  Effect,
  help,
  error,
  loadBoard,
  initJavaFx,
  createSceneDrawer,
  startStepper,
  startAnimator
}
import java.util.concurrent.atomic.AtomicReference
import javafx.scene.paint.Color

class CanvasConfig(grid: Grid, config: Config) {
  private val cellWidth = config.width
  private val cellMargin = config.margin

  def height(): Double = ((cellWidth + cellMargin) * grid.height + cellMargin).toDouble
  def width(): Double = ((cellWidth + cellMargin) * grid.width + cellMargin).toDouble
  def color(): Color = Function.tupled[Int, Int, Int, Color](Color.rgb)(config.bgColor)
}

object GridRef {
  type GridRef = AtomicReference[Option[Grid]]
}

object Main {
  def main(params: Params): Effect[Unit] = {
    Config.parse(params.unnamed, params.named) match {
      case Help => help()
      case Error(s) => error(s)
      case Go(config) => runGame(config)
    }
  }

  def runGame(config: Config): Effect[Unit] =
    for {
      eitherBoardStr <- loadBoard(config.boardSource)
      _ <- continue(eitherBoardStr, config)
    } yield (): Unit

  def continue(eitherBoardStr: Either[String, String], config: Config): Effect[Unit] =
    eitherBoardStr match {
      case Left(s) => error(s)
      case Right(boardString) => maybeStartGfx(Grid.build(boardString), config)
    }

  def maybeStartGfx(eitherGrid: Either[String, Grid], config: Config): Effect[Unit] =
    eitherGrid match {
      case Left(s) => error(s)
      case Right(grid) => startGfx(grid, config)
    }

  def startGfx(grid: Grid, config: Config): Effect[Unit] = {
    val canvasConfig = new CanvasConfig(grid, config)
    val width = canvasConfig.width()
    val height = canvasConfig.height()
    val color = canvasConfig.color()
    val gridRef = new AtomicReference[Option[Grid]](Some(grid))
    for {
      boxDrawer <- initJavaFx(width, height, color)
      sceneDrawer <- createSceneDrawer(config, boxDrawer)
      _ <- startStepper(gridRef, config.timeDelta, grid)
      _ <- startAnimator(gridRef, sceneDrawer)
    } yield (): Unit
  }
}
