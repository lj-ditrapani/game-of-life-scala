package info.ditrapani.gameoflife

import cats.~>
import effects.EffectA
import effects.{Help, Error, LoadBoard, InitJavaFx, CreateSceneDrawer, StartStepper, StartAnimator}
import javafx.application.Application
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.{Group, Scene}
import javafx.stage.Stage
import monix.eval.Task
import terminator.{Terminator, PrinterImpl, KillerImpl, HelpTextLoaderImpl}

class Params(parameters: Application.Parameters) {
  import scala.collection.JavaConverters._
  def unnamed: List[String] = parameters.getUnnamed.asScala.toList
  def named: Map[String, String] = parameters.getNamed.asScala.toMap
}

class JavaFxInit(stage: Stage) {
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
  import monix.execution.Scheduler.Implicits.global
  override def start(stage: Stage): Unit =
    Main
      .main(new Params(getParameters()))
      .foldMap(new Compiler(stage))
      .runToFuture
      .onComplete(_ => (): Unit)
}

class Compiler(stage: Stage) extends (EffectA ~> Task) {
  import monix.execution.Scheduler.Implicits.global
  private val terminator = new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl)
  private val javaFxInit = new JavaFxInit(stage)

  def apply[A](fa: EffectA[A]): Task[A] =
    fa match {
      case Help =>
        Task.eval { terminator.help() }
      case Error(s) =>
        Task.eval { terminator.error(s) }
      case LoadBoard(boardSource) =>
        Task.eval { BoardLoader.getBoardStr(boardSource) }
      case InitJavaFx(width, height, color) =>
        Task.eval { javaFxInit.startApp(width, height, color) }
      case CreateSceneDrawer(config, boxDrawer) =>
        Task.eval { new SceneDrawer(config, boxDrawer) }
      case StartStepper(gridRef, timeDelta, grid) =>
        Task.eval {
          new Stepper(gridRef, timeDelta).run(grid, Infinity).runToFuture
          (): Unit
        }
      case StartAnimator(gridRef, sceneDrawer) =>
        Task.eval { new Animator(gridRef, sceneDrawer).run() }
    }
}
