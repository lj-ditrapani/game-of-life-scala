package info.ditrapani.gameoflife.javafxinit

import cats.free.Free
import cats.free.Free.liftF
import info.ditrapani.gameoflife.{BoxDrawer, CanvasDimensions, config, Grid}
import scalafx.scene.paint.Color
import config.Config

sealed trait JavaFXA[A]
final case class CreateCanvas(width: Double, height: Double) extends JavaFXA[Unit]
final case class CreateGfxContext(color: Color, width: Double, height: Double) extends JavaFXA[Unit]
final case class SetStage(width: Double, height: Double) extends JavaFXA[Unit]
object CreateBoxDrawer extends JavaFXA[BoxDrawer]

object JavaFXInit {
  type JavaFX[A] = Free[JavaFXA, A]

  def createCanvas(width: Double, height: Double): JavaFX[Unit] =
    liftF[JavaFXA, Unit](CreateCanvas(width, height))

  def createGfxContext(color: Color, width: Double, height: Double): JavaFX[Unit] =
    liftF[JavaFXA, Unit](CreateGfxContext(color, width, height))

  def setStage(width: Double, height: Double): JavaFX[Unit] =
    liftF[JavaFXA, Unit](SetStage(width, height))

  def createBoxDrawer(): JavaFX[BoxDrawer] =
    liftF[JavaFXA, BoxDrawer](CreateBoxDrawer)

  def createStageAndBoxDrawer(grid: Grid, config: Config): JavaFX[BoxDrawer] = {
    val canvasDimensions = new CanvasDimensions(grid, config)
    val height = canvasDimensions.height()
    val width = canvasDimensions.width()

    for {
      _ <- createCanvas(width, height)
      _ <- createGfxContext(
        Function.tupled[Int, Int, Int, Color](Color.rgb)(config.bgColor),
        width,
        height
      )
      _ <- setStage(width, height)
      boxDrawer <- createBoxDrawer
    } yield boxDrawer
  }
}
