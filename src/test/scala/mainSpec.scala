package info.ditrapani.gameoflife

import cats.{Id, ~>}
import config.{BuiltIn, Config}
import effects.EffectA
import effects.{Help, Error, LoadBoard, InitJavaFx, CreateSceneDrawer, StartStepper, StartAnimator}
import javafx.scene.paint.Color
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import org.scalatest.mockito.MockitoSugar

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

class MainSpec extends Spec with MockitoSugar with EitherValues {
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
        Main.main(params).foldMap(helpCompiler)
        helpCompiler.helpCalled shouldBe true
      }
    }

    describe("when the boardLoader returns an Error") {
      it("terminates with an Error effect") {
        val params = mock[Params]
        when(params.unnamed).thenReturn(List[String]())
        when(params.named).thenReturn(Map[String, String]("b" -> "1"))
        val errorCompiler = new ErrorCompiler
        Main.main(params).foldMap(errorCompiler)
        errorCompiler.errorCalled shouldBe true
      }
    }

    describe("when running the happy path") {
      it("goes through a bunch of effects in ordor") {
        val params = mock[Params]
        when(params.unnamed).thenReturn(List[String]())
        when(params.named).thenReturn(Map[String, String]("b" -> "1"))
        val testCompiler = new TestCompiler
        Main.main(params).foldMap(testCompiler)
        testCompiler.animatorCalled shouldBe true
      }
    }
  }
}
