package info.ditrapani.gameoflife.effects

import info.ditrapani.gameoflife.GridRef.GridRef
import info.ditrapani.gameoflife.{BoxDrawer, SceneDrawer}
import info.ditrapani.gameoflife.config.{BoardSource, Config}
import javafx.scene.paint.Color

sealed trait EffectA[A]
object Help extends EffectA[Unit]
final case class Error(message: String) extends EffectA[Unit]
final case class LoadBoard(boardSource: BoardSource) extends EffectA[Either[String, String]]
final case class InitJavaFx(width: Double, height: Double, color: Color) extends EffectA[BoxDrawer]
final case class CreateSceneDrawer(config: Config, boxDrawer: BoxDrawer)
    extends EffectA[SceneDrawer]
final case class StartStepper(gridRef: GridRef, timeDelta: Int) extends EffectA[Unit]
final case class StartAnimator(gridRef: GridRef, sceneDrawer: SceneDrawer) extends EffectA[Unit]

object Effects {
  import cats.free.Free
  import cats.free.Free.liftF

  type Effect[A] = Free[EffectA, A]

  def help(): Effect[Unit] =
    liftF[EffectA, Unit](Help)

  def error(message: String): Effect[Unit] =
    liftF[EffectA, Unit](Error(message))

  def loadBoard(boardSource: BoardSource): Effect[Either[String, String]] =
    liftF[EffectA, Either[String, String]](LoadBoard(boardSource))

  def initJavaFx(width: Double, height: Double, color: Color): Effect[BoxDrawer] =
    liftF[EffectA, BoxDrawer](InitJavaFx(width, height, color))

  def createSceneDrawer(config: Config, boxDrawer: BoxDrawer): Effect[SceneDrawer] =
    liftF[EffectA, SceneDrawer](CreateSceneDrawer(config, boxDrawer))

  def startStepper(gridRef: GridRef, timeDelta: Int): Effect[Unit] =
    liftF[EffectA, Unit](StartStepper(gridRef, timeDelta))

  def startAnimator(gridRef: GridRef, sceneDrawer: SceneDrawer): Effect[Unit] =
    liftF[EffectA, Unit](StartAnimator(gridRef, sceneDrawer))
}
