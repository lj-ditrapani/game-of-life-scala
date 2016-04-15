package info.ditrapani.game_of_life

import org.scalatest.{FunSpec, Matchers}

class LifeSpec extends FunSpec with Matchers {
  describe("Grid") {
  }

  describe("Cell") {
    describe("alive") {
      it("it returns false if dead") {
        Cell(false).alive should be (false)
      }

      it("it returns true if alive") {
        Cell(true).alive should be (true)
      }
    }
  }

  describe("Grid Class") {
  }

  describe("Grid Object") {
    describe("line_lengths_match") {
      it("returns true if all lines are the same length") {
        val lines = Vector("--++-", "+-+-+", "++--+")
        Grid.line_lengths_match(lines) should === (true)
      }

      it("returns false if the second line differs in length") {
        val lines = Vector("+-+-+", "+-+-", "+-+-")
        Grid.line_lengths_match(lines) should === (false)
      }

      it("returns false the last line differs in length") {
        val lines = Vector("+-+-+", "+-+-+", "+-+-")
        Grid.line_lengths_match(lines) should === (false)
      }
    }
  }

  describe("Adder") {
    describe("add") {
      it("add an int b and returns Adder(a + b)") {
        Adder(5).add(6) should be (Adder(11))
      }
    }

    describe("+") {
      it("adds to adders together") {
        (Adder(5) + Adder(6)) should be (Adder(11))
      }
    }
  }
}
