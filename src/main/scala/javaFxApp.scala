package info.ditrapani.gameoflife

import javafx.application.Application
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.{Group, Scene}
import javafx.stage.Stage
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

class Params(parameters: Application.Parameters) {
  import scala.collection.JavaConverters._
  def unnamed: List[String] = parameters.getUnnamed.asScala.toList
  def named: Map[String, String] = parameters.getNamed.asScala.toMap
}

trait JavaFxInit {
  def startApp(width: Double, height: Double, color: Color): BoxDrawer
}

class JavaFxInitImpl(stage: Stage) extends JavaFxInit {
  def startApp(width: Double, height: Double, color: Color): BoxDrawer = {
    val canvas = new Canvas(width, height)
    canvas.setTranslateX(0)
    canvas.setTranslateY(0)

    val gc = canvas.getGraphicsContext2D()
    gc.setFill(color)
    gc.fillRect(0, 0, width, height)

    val root = new Group()
    root.getChildren().add(canvas)
    val scene = new Scene(root, width, height)
    stage.setTitle("Game of Life by L. J. Di Trapani")
    stage.setScene(scene)
    stage.sizeToScene()
    stage.show()
    new BoxDrawerImpl(gc)
  }
}

object JavaFxApp {
  def main(args: Array[String]): Unit = Application.launch(classOf[JavaFxApp], args: _*)
}

class JavaFxApp extends Application {
  override def start(stage: Stage): Unit =
    new Main(
      BoardLoaderImpl,
      new JavaFxInitImpl(stage),
      SceneDrawerFactoryImpl,
      AnimatorFactoryImpl,
      StepperFactoryImpl
    ).main(
      new Params(getParameters()),
      new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl)
    )
}
