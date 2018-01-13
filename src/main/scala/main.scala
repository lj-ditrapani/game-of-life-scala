package info.ditrapani.gameoflife

import cats.{Id, ~>}
import config.Config
import javafxinit.{JavaFXInit, JavaFXA, CreateCanvas, CreateGfxContext, SetStage, CreateBoxDrawer}
import scalafx.application.JFXApp
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.Scene
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

trait JavaFxApp {
  def createStageAndBoxDrawer(grid: Grid, config: Config): BoxDrawer
}

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

object NewMain extends JFXApp with JavaFxApp {

  new Life(BoardLoaderImpl, this, SceneDrawerFactoryImpl, AnimatorFactoryImpl, StepperFactoryImpl)
    .main(new Params(parameters), new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl))

  def createStageAndBoxDrawer(grid: Grid, config: Config): BoxDrawer =
    JavaFXInit.createStageAndBoxDrawer(grid, config).foldMap(impureCompiler)

  @SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.Var"))
  private def impureCompiler: JavaFXA ~> Id =
    new (JavaFXA ~> Id) {

      var canvas: Canvas = null
      var gc: GraphicsContext = null

      def apply[A](fa: JavaFXA[A]): Id[A] =
        fa match {
          case CreateCanvas(width, height) =>
            canvas = new Canvas(width, height)
            canvas.translateX = 0
            canvas.translateY = 0
            (): Unit
          case CreateGfxContext(color, width, height) =>
            gc = canvas.graphicsContext2D
            gc.setFill(color)
            gc.fillRect(0, 0, width, height)
            (): Unit
          case SetStage(w, h) =>
            stage = new JFXApp.PrimaryStage {
              title = "Game of Life by L. J. Di Trapani"
              scene = new Scene(w.toDouble, h.toDouble) {
                content = canvas
              }
            }
            (): Unit
          case CreateBoxDrawer =>
            new BoxDrawerImpl(gc)
        }
    }
}
