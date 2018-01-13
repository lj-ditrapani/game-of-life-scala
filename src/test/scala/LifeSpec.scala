package info.ditrapani.gameoflife

import config.{BoardSource, Config}
import java.util.concurrent.atomic.AtomicReference
import monix.eval.Task
import monix.execution.Scheduler
import org.mockito.ArgumentMatchers.{any, anyString, eq => meq}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.EitherValues
import org.scalatest.mockito.MockitoSugar
import terminator.Terminator

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class LifeSpec extends Spec with MockitoSugar with EitherValues {
  describe("main") {
    it("calls printErrorHelpAndExit when the Config.parse returns a Left") {
      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource.Source): Either[String, String] =
          Right("---\n--+\n+++")
      }

      val javaFxApp = mock[JavaFxApp]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      when(params.unnamed).thenReturn(List[String]("--help"))
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))

      val life = new Life(
        BoardLoaderFake,
        javaFxApp,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(terminator).printErrorHelpAndExit("Printing help text...")
    }

    it("calls printErrorHelpAndExit when the boardLoader returns a Left") {
      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource.Source): Either[String, String] =
          Left("Fire!")
      }

      val javaFxApp = mock[JavaFxApp]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      when(params.unnamed).thenReturn(List[String]())
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))

      val life = new Life(
        BoardLoaderFake,
        javaFxApp,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(terminator).printErrorHelpAndExit("Fire!")
    }

    it("calls a bunch of functions") {
      val boardStr = "---\n--+\n+++"

      object BoardLoaderFake extends BoardLoader {
        def getBoardStr(boardSource: BoardSource.Source): Either[String, String] =
          Right(boardStr)
      }

      val javaFxApp = mock[JavaFxApp]
      val sceneDrawerFactory = mock[SceneDrawerFactory]
      val animatorFactory = mock[AnimatorFactory]
      val stepperFactory = mock[StepperFactory]
      val params = mock[Params]
      val terminator = mock[Terminator]

      val grid = Grid.build(boardStr).right.value
      val config = Config.defaultConfig(BoardSource.BuiltIn(0))
      val boxDrawer = mock[BoxDrawer]
      val sceneDrawer = mock[SceneDrawer]
      val animator = mock[Animator]
      val stepper = mock[Stepper]
      val task = mock[Task[Unit]]

      when(params.unnamed).thenReturn(List[String]())
      when(params.named).thenReturn(Map[String, String]("b" -> "1"))
      when(javaFxApp.createStageAndBoxDrawer(grid, config)).thenReturn(boxDrawer)
      when(sceneDrawerFactory.apply(config, boxDrawer)).thenReturn(sceneDrawer)
      when(animatorFactory.apply(any[AtomicReference[Option[Grid]]], meq(sceneDrawer)))
        .thenReturn(animator)
      when(stepperFactory.apply(any[AtomicReference[Option[Grid]]], meq(config.timeDelta)))
        .thenReturn(stepper)
      when(stepper.run(grid, Infinity)).thenReturn(task)

      val life = new Life(
        BoardLoaderFake,
        javaFxApp,
        sceneDrawerFactory,
        animatorFactory,
        stepperFactory
      )
      life.main(params, terminator)
      verify(params).unnamed
      verify(params).named
      verify(javaFxApp).createStageAndBoxDrawer(grid, config)
      verify(sceneDrawerFactory).apply(config, boxDrawer)
      verify(animatorFactory).apply(any[AtomicReference[Option[Grid]]], meq(sceneDrawer))
      verify(stepperFactory).apply(any[AtomicReference[Option[Grid]]], meq(config.timeDelta))
      verify(stepper).run(grid, Infinity)
      verify(task).runAsync(any[Scheduler])
      verify(terminator, never()).printErrorHelpAndExit(anyString)
    }
  }
}
