package info.ditrapani.game_of_life

import org.scalatest.{FunSpec, Matchers}

class LifeSpec extends FunSpec with Matchers {
  describe("Game of Life") {
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
