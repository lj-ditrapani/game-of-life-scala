package info.ditrapani.gameoflife

import config.{BoardSource, Config}
import java.util.concurrent.atomic.AtomicReference
import javafx.scene.paint.Color

object GridRef {
  type GridRef = AtomicReference[Option[Grid]]
}

sealed trait EffectA[A]
final case class Terminate(message: String) extends EffectA[Unit]
final case class LoadBoard(boardSource: BoardSource) extends EffectA[Either[String, String]]
final case class InitJavaFx(width: Double, height: Double, color: Color) extends EffectA[BoxDrawer]
final case class CreateSceneDrawer(config: Config, boxDrawer: BoxDrawer)
    extends EffectA[SceneDrawer]
final case class StartStepper(gridRef: GridRef.GridRef, timeDelta: Int) extends EffectA[Unit]
final case class StartAnimator(gridRef: GridRef.GridRef, sceneDrawer: SceneDrawer)
    extends EffectA[Unit]

object Effects {
  import cats.free.Free
  import cats.free.Free.liftF

  type Effect[A] = Free[EffectA, A]

  def terminate(message: String): Effect[Unit] =
    liftF[EffectA, Unit](Terminate(message))

  def loadBoard(boardSource: BoardSource): Effect[Either[String, String]] =
    liftF[EffectA, Either[String, String]](LoadBoard(boardSource))

  def initJavaFx(width: Double, height: Double, color: Color): Effect[BoxDrawer] =
    liftF[EffectA, BoxDrawer](InitJavaFx(width, height, color))

  def createSceneDrawer(config: Config, boxDrawer: BoxDrawer): Effect[SceneDrawer] =
    liftF[EffectA, SceneDrawer](CreateSceneDrawer(config, boxDrawer))

  def startStepper(gridRef: GridRef.GridRef, timeDelta: Int): Effect[Unit] =
    liftF[EffectA, Unit](StartStepper(gridRef, timeDelta))

  def startAnimator(gridRef: GridRef.GridRef, sceneDrawer: SceneDrawer): Effect[Unit] =
    liftF[EffectA, Unit](StartAnimator(gridRef, sceneDrawer))
}

object NewMain {
  import Effects.{terminate, loadBoard, initJavaFx, createSceneDrawer, startStepper, startAnimator}
  type Effect[A] = Effects.Effect[A]

  def main(params: Params): Effect[Unit] = {
    Config.parse(params.unnamed, params.named) match {
      case Left(s) => terminate(s)
      case Right(config) => runGame(config)
    }
  }

  def runGame(config: Config): Effect[Unit] =
    for {
      eitherBoardStr <- loadBoard(config.boardSource)
      x <- next(eitherBoardStr, config)
    } yield x

  def next(eitherBoardStr: Either[String, String], config: Config): Effect[Unit] =
    eitherBoardStr match {
      case Left(s) => terminate(s)
      case Right(boardString) => maybeStartGfx(Grid.build(boardString), config)
    }

  def maybeStartGfx(eitherGrid: Either[String, Grid], config: Config): Effect[Unit] =
    eitherGrid match {
      case Left(s) => terminate(s)
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
      _ <- startStepper(gridRef, config.timeDelta)
      _ <- startAnimator(gridRef, sceneDrawer)
    } yield (): Unit
  }
}
