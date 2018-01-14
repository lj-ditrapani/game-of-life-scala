package info.ditrapani.gameoflife

import config.{BoardSource, BuiltIn, Config}
import java.util.concurrent.atomic.AtomicReference
import monix.eval.Task
import monix.execution.Scheduler
import org.mockito.ArgumentMatchers.{any, anyString, eq => meq}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.EitherValues
import org.scalatest.mockito.MockitoSugar
import javafx.scene.paint.Color
import terminator.Terminator

class CanvasConfigSpec extends Spec {

  private val canvasConfig = {
    val grid = Grid(
      Vector(
        Vector(Cell.living, Cell.dead, Cell.living),
        Vector(Cell.living, Cell.dead, Cell.dead)
      )
    )
    val config = Config.defaultConfig(BuiltIn(2))
    new CanvasConfig(grid, config)
  }

  describe("height") {
    it("returns the canvas height") {
      canvasConfig.height shouldBe 44.0 // (16.0 + 4.0) * 2 + 4.0
    }
  }

  describe("width") {
    it("returns the canvas width") {
      canvasConfig.width shouldBe 64.0 // (16.0 + 4.0) * 3 + 4.0
    }
  }

  describe("color") {
    it("returns the canvas background color") {
      canvasConfig.color shouldBe Color.rgb(150, 170, 200)
    }
  }
}

import cats.{Id, ~>}
import effects.EffectA
import effects.{Help, Error, LoadBoard, InitJavaFx, CreateSceneDrawer, StartStepper, StartAnimator}
import org.scalatest.EitherValues

class NewMainSpec extends Spec with MockitoSugar with EitherValues {
  private val boardStr = "---\n-+-\n+++"
  private val grid = Grid.build(boardStr).right.value

  class HelpCompiler extends (EffectA ~> Id) {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var helpCalled = false

    def apply[A](fa: EffectA[A]): Id[A] =
      fa match {
        case Help =>
          helpCalled = true
          (): Unit
        case _ =>
          fail()
      }
  }

  class ErrorCompiler extends (EffectA ~> Id) {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var errorCalled = false

    def apply[A](fa: EffectA[A]): Id[A] =
      fa match {
        case Help =>
          fail()
        case Error(s) =>
          s shouldBe "the error message"
          errorCalled = true
        case LoadBoard(boardSource) =>
          boardSource shouldBe BuiltIn(0)
          Left[String, String]("the error message")
        case _ =>
          fail()
      }
  }

  class TestCompiler extends (EffectA ~> Id) {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var animatorCalled = false
    private val boxDrawer = mock[BoxDrawer]
    private val sceneDrawer = mock[SceneDrawer]

    def apply[A](fa: EffectA[A]): Id[A] =
      fa match {
        case LoadBoard(boardSource) =>
          boardSource shouldBe BuiltIn(0)
          Right[String, String](boardStr)
        case InitJavaFx(width, height, color) =>
          width shouldBe 64.0
          height shouldBe 64.0
          color shouldBe Color.rgb(150, 170, 200)
          boxDrawer
        case CreateSceneDrawer(config, boxDrawerArg) =>
          config shouldBe Config.defaultConfig(BuiltIn(0))
          boxDrawerArg shouldBe boxDrawer
          sceneDrawer
        case StartStepper(gridRef, timeDelta, gridArg) =>
          gridRef.get() shouldBe Some(gridArg)
          timeDelta shouldBe 500
          gridArg shouldBe grid
          (): Unit
        case StartAnimator(gridRef, sceneDrawerArg) =>
          gridRef.get() shouldBe Some(grid)
          sceneDrawerArg shouldBe sceneDrawer
          animatorCalled = true
        case _ =>
          fail()
      }
  }

  describe("main") {
    describe("when --help is specified") {
      it("runs the program") {
        val params = mock[Params]
        when(params.unnamed).thenReturn(List[String]("--help"))
        when(params.named).thenReturn(Map[String, String]("b" -> "1"))
        val helpCompiler = new HelpCompiler
        NewMain.main(params).foldMap(helpCompiler)
        helpCompiler.helpCalled shouldBe true
      }
    }

    describe("when the boardLoader returns an Error") {
      it("terminates with an Error effect") {
        val params = mock[Params]
        when(params.unnamed).thenReturn(List[String]())
        when(params.named).thenReturn(Map[String, String]("b" -> "1"))
        val errorCompiler = new ErrorCompiler
        NewMain.main(params).foldMap(errorCompiler)
        errorCompiler.errorCalled shouldBe true
      }
    }

    describe("when running the happy path") {
      it("goes through a bunch of effects in ordor") {
        val params = mock[Params]
        when(params.unnamed).thenReturn(List[String]())
        when(params.named).thenReturn(Map[String, String]("b" -> "1"))
        val testCompiler = new TestCompiler
        NewMain.main(params).foldMap(testCompiler)
        testCompiler.animatorCalled shouldBe true
      }
    }
  }
}

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class MainSpec extends Spec with MockitoSugar with EitherValues {
  describe("main") {
    it("calls terminator.help when the Config.parse returns a Help") {
      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource): Either[String, String] =
          Right("---\n--+\n+++")
      }

      val javaFxInit = mock[JavaFxInit]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      when(params.unnamed).thenReturn(List[String]("--help"))
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))

      val life = new Main(
        BoardLoaderFake,
        javaFxInit,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(terminator).help()
    }

    it("calls terminator.error when the boardLoader returns an Error") {
      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource): Either[String, String] =
          Left("Fire!")
      }

      val javaFxInit = mock[JavaFxInit]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      when(params.unnamed).thenReturn(List[String]())
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))

      val life = new Main(
        BoardLoaderFake,
        javaFxInit,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(terminator).error("Fire!")
    }

    it("calls a bunch of functions") {
      val boardStr = "---\n--+\n+++"

      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource): Either[String, String] =
          Right(boardStr)
      }

      val javaFxInit = mock[JavaFxInit]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      val width = 64.0
      val height = 64.0
      val color = Color.rgb(150, 170, 200)

      val grid = Grid.build(boardStr).right.value
      val config = Config.defaultConfig(BuiltIn(0))
      val boxDrawer = mock[BoxDrawer]
      val sceneDrawer = mock[SceneDrawer]
      val animator = mock[Animator]
      val stepper = mock[Stepper]
      val task = mock[Task[Unit]]

      when(params.unnamed).thenReturn(List[String]())
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))
      when(javaFxInit.startApp(width, height, color)).thenReturn(boxDrawer)
      when(sceneDrawerFactory.apply(config, boxDrawer)).thenReturn(sceneDrawer)
      when(animatorFactory.apply(any[AtomicReference[Option[Grid]]], meq(sceneDrawer)))
        .thenReturn(animator)
      when(stepperFactory.apply(any[AtomicReference[Option[Grid]]], meq(config.timeDelta)))
        .thenReturn(stepper)
      when(stepper.run(grid, Infinity)).thenReturn(task)

      val life = new Main(
        BoardLoaderFake,
        javaFxInit,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(params).unnamed
      verify(params).named
      verify(javaFxInit).startApp(width, height, color)
      verify(sceneDrawerFactory).apply(config, boxDrawer)
      verify(animatorFactory).apply(any[AtomicReference[Option[Grid]]], meq(sceneDrawer))
      verify(stepperFactory).apply(any[AtomicReference[Option[Grid]]], meq(config.timeDelta))
      verify(stepper).run(grid, Infinity)
      verify(task).runAsync(any[Scheduler])
      verify(terminator, never()).help()
      verify(terminator, never()).error(anyString)
    }
  }
}
