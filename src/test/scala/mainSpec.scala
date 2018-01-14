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

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class MainSpec extends Spec with MockitoSugar with EitherValues {
  describe("main") {
    it("calls printErrorHelpAndExit when the Config.parse returns a Left") {
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

    it("calls printErrorHelpAndExit when the boardLoader returns a Left") {
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
