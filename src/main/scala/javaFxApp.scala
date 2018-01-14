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

import cats.{Id, ~>}
import effects.EffectA
import effects.{Help, Error, LoadBoard, InitJavaFx, CreateSceneDrawer, StartStepper, StartAnimator}

object JavaFxApp2 {
  def main(args: Array[String]): Unit = Application.launch(classOf[JavaFxApp2], args: _*)
}

class JavaFxApp2 extends Application {
  override def start(stage: Stage): Unit =
    NewMain.main(new Params(getParameters())).foldMap(new Compiler(stage))
}

class Compiler(stage: Stage) extends (EffectA ~> Id) {
  import monix.execution.Scheduler.Implicits.global
  private val terminator = new Terminator(PrinterImpl, KillerImpl, HelpTextLoaderImpl)
  private val javaFxInit = new JavaFxInitImpl(stage)

  def apply[A](fa: EffectA[A]): Id[A] =
    fa match {
      case Help =>
        println("help!")
        terminator.help()
      case Error(s) =>
        println(s"[error]: $s")
        terminator.error(s)
      case LoadBoard(boardSource) =>
        println(s"LoadBoard: $boardSource")
        BoardLoaderImpl.getBoardStr(boardSource)
      case InitJavaFx(width, height, color) =>
        println(s"initJavaFx $width $height $color")
        javaFxInit.startApp(width, height, color)
      case CreateSceneDrawer(config, boxDrawer) =>
        println(s"CreateSceneDrawer: $config $boxDrawer")
        new SceneDrawer(config, boxDrawer)
      case StartStepper(gridRef, timeDelta, grid) =>
        println(s"StartStepper: $gridRef $timeDelta")
        new Stepper(gridRef, timeDelta).run(grid, Infinity).runAsync
        (): Unit
      case StartAnimator(gridRef, sceneDrawer) =>
        println(s"StartAnimator: $gridRef $sceneDrawer")
        new AnimatorImpl(gridRef, sceneDrawer).run()
    }
}
