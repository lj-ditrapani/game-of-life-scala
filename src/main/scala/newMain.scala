package info.ditrapani.gameoflife

import config.{Config, Error, Go, Help}
import effects.Effects
import java.util.concurrent.atomic.AtomicReference

object GridRef {
  type GridRef = AtomicReference[Option[Grid]]
}

object NewMain {
  import Effects.{
    help,
    error,
    loadBoard,
    initJavaFx,
    createSceneDrawer,
    startStepper,
    startAnimator
  }
  type Effect[A] = Effects.Effect[A]

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
