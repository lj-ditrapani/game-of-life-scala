package info.ditrapani.gameoflife.javafxinit

import info.ditrapani.gameoflife.config.{BoardSource, Config}
import info.ditrapani.gameoflife.{BoxDrawer, Cell, Grid, Spec}

class CanvasDimensionsSpec extends Spec {

  private val canvasDimensions = {
    val grid = Grid(
      Vector(
        Vector(Cell.living, Cell.dead, Cell.living),
        Vector(Cell.living, Cell.dead, Cell.dead)
      )
    )
    val config = Config.defaultConfig(BoardSource.BuiltIn(2))
    new CanvasDimensions(grid, config)
  }

  describe("height") {
    it("returns the canvas height") {
      canvasDimensions.height shouldBe 44.0 // (16.0 + 4.0) * 2 + 4.0
    }
  }

  describe("width") {
    it("returns the canvas width") {
      canvasDimensions.width shouldBe 64.0 // (16.0 + 4.0) * 3 + 4.0
    }
  }
}

import cats.{Id, ~>}
import org.scalatest.EitherValues
import scalafx.scene.paint.Color

class TestBoxDrawer extends BoxDrawer {
  def draw(color: Color, x: Int, y: Int, width: Int): Unit = ???
}

class JavaFXInitSpec extends Spec with EitherValues {
  private val boxDrawer = new TestBoxDrawer()

  object TestCompiler extends (JavaFXA ~> Id) {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    private var step = 0

    def apply[A](fa: JavaFXA[A]): Id[A] = {
      step += 1
      fa match {
        case CreateCanvas(width, height) =>
          step shouldBe 1
          width shouldBe 84 // 4 * (16 + 4) + 4
          height shouldBe 64 // 3 * (16 + 4) + 4
          (): Unit
        case CreateGfxContext(color, width, height) =>
          step shouldBe 2
          color shouldBe Color.rgb(150, 170, 200) // default config background color
          width shouldBe 84
          height shouldBe 64
          (): Unit
        case SetStage(width, height) =>
          step shouldBe 3
          width shouldBe 84
          height shouldBe 64
          (): Unit
        case CreateBoxDrawer =>
          step shouldBe 4
          boxDrawer
      }
    }
  }

  describe("createStageAndBoxDrawer") {
    it("runs the effects in order with the correct parameters and returns the correct result") {
      val grid = Grid.build("----\n-+-+\n++++").right.value
      val config = Config.defaultConfig(BoardSource.BuiltIn(2))
      JavaFXInit.createStageAndBoxDrawer(grid, config).foldMap(TestCompiler) shouldBe boxDrawer
    }
  }
}
