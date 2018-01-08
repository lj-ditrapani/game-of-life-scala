package info.ditrapani.gameoflife

import config.{BoardSource, Config}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import scalafx.application.JFXApp

import scala.collection.mutable.{Map => MutableMap}

@SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
class ParamsSpec extends Spec with MockitoSugar {
  private val parameters = mock[JFXApp.Parameters]

  describe("unnamed") {
    it("converts the unnamed parameters from Seq to a List") {
      when(parameters.unnamed).thenReturn(Seq("a", "b"))
      val p = new Params(parameters)
      p.unnamed shouldBe List("a", "b")
    }
  }

  describe("named") {
    it("converts the named parameters from a pontential mutable Map to an immutable Map") {
      when(parameters.named).thenReturn(MutableMap("a" -> "1", "b" -> "2"))
      val p = new Params(parameters)
      p.named shouldBe Map("a" -> "1", "b" -> "2")
    }
  }
}

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
