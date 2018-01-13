package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.Scene
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

trait JavaFxApp {
  def init(width: Double, height: Double, color: Color): BoxDrawer
}

class Params(parameters: JFXApp.Parameters) {
  def unnamed: List[String] = parameters.unnamed.toList
  def named: Map[String, String] = Map(parameters.named.toSeq: _*)
}

object JavaFxAppImpl extends JFXApp with JavaFxApp {
  new Main(BoardLoaderImpl, this, SceneDrawerFactoryImpl, AnimatorFactoryImpl, StepperFactoryImpl)
    .main(new Params(parameters), new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl))

  def init(width: Double, height: Double, color: Color): BoxDrawer = {
    val canvas = new Canvas(width, height)
    canvas.translateX = 0
    canvas.translateY = 0

    val gc = canvas.graphicsContext2D
    gc.setFill(color)
    gc.fillRect(0, 0, width, height)

    val (w, h) = (width, height)
    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(w, h) {
        content = canvas
      }
    }

    new BoxDrawerImpl(gc)
  }
}
