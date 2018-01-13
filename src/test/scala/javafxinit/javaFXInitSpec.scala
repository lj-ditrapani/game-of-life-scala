package info.ditrapani.gameoflife.javafxinit

import info.ditrapani.gameoflife.config.{BoardSource, Config}
import info.ditrapani.gameoflife.{Cell, Grid, Spec}

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
