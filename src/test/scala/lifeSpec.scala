package info.ditrapani.game_of_life

import org.scalatest.{FunSpec, Matchers}

class LifeSpec extends FunSpec with Matchers {
  describe("Grid") {
  }

  describe("Cell Class") {
    describe("alive") {
      it("it returns false if dead") {
        Cell(false).alive should be (false)
      }

      it("it returns true if alive") {
        Cell(true).alive should be (true)
      }
    }
  }

  describe("Cell Object") {
    describe("from_char") {
      it("returns a dead cell if char is -") {
        Cell.from_char('-') should === (Cell(false))
      }

      it("returns an alive cell if char is +") {
        Cell.from_char('+') should === (Cell(true))
      }
    }
  }

  describe("Grid Class") {
  }

  describe("Grid Object") {
    describe("build") {
      it("returns Left if rows < 3") {
        val str = "--++-\n+-+-+"
        Grid.build(str) should === (Left("Board must be at least 3 x 3"))
      }

      it("returns Left if columns < 3") {
        val str = "--\n+-\n++"
        Grid.build(str) should === (Left("Board must be at least 3 x 3"))
      }

      it("returns Left if line lengths don't match") {
        val str = "--+\n+-\n+++"
        Grid.build(str) should === (Left("Board line lengths don't match"))
      }

      it("returns Left if any char is not + or -") {
        val str = "--+\n+?-\n+++"
        Grid.build(str) should === (
          Left("Board must contain only + and - characters")
        )
      }

      it("returns Right(Grid) if the str is legal") {
        val str = "--+\n-+-\n+--"
        Grid.build(str) should === (
          Right(
            Grid(
              Vector(
                Vector(Cell(false), Cell(false), Cell(true)),
                Vector(Cell(false), Cell(true), Cell(false)),
                Vector(Cell(true), Cell(false), Cell(false))
              )
            )
          )
        )
      }
    }
    describe("line_lengths_match") {
      it("returns true if all lines are the same length") {
        val lines = Vector("--++-", "+-+-+", "++--+")
        Grid.line_lengths_match(lines) should be (true)
      }

      it("returns false if the second line differs in length") {
        val lines = Vector("+-+-+", "+-+-", "+-+-")
        Grid.line_lengths_match(lines) should be (false)
      }

      it("returns false the last line differs in length") {
        val lines = Vector("+-+-+", "+-+-+", "+-+-")
        Grid.line_lengths_match(lines) should be (false)
      }
    }

    describe("only_pluses_and_dashes") {
      it("returns true if all lines only have +'s & -'s") {
        val lines = Vector("--++-", "+-+-+", "++--+")
        Grid.only_pluses_and_dashes(lines) should be (true)
      }

      it("returns false if any line contains something else") {
        val lines = Vector("--++-", "+-+-+", "++|-+")
        Grid.only_pluses_and_dashes(lines) should be (false)
      }
    }
  }
}
