package info.ditrapani.game_of_life

import org.scalatest.{FunSpec, Matchers}

class LifeSpec extends FunSpec with Matchers {
  describe("Grid") {
  }

  describe("Cell Class") {
    describe("alive") {
      it("returns false if dead") {
        Cell.dead.alive should be (false)
      }

      it("returns true if alive") {
        Cell.living.alive should be (true)
      }
    }

    describe("to_char") {
      it("returns + if alive") {
        Cell.living.to_char should be ('+')
      }

      it("it returns - if dead") {
        Cell.dead.to_char should be ('-')
      }
    }

    describe("next") {
      def run_tests(base_cell: Cell, tests: List[(Int, Cell)]): Unit = {
        for (test <- tests) {
          val (count, expected_cell) = test
          val state = if (expected_cell.alive) "living" else "dead"
          it(s"returns the $state Cell if neighbor_count == $count") {
            base_cell.next(count) should be (expected_cell)
          }
        }
      }

      describe("when cell is alive,") {
        val tests = List(
          (0 -> Cell.dead),
          (1 -> Cell.dead),
          (2 -> Cell.living),
          (3 -> Cell.living),
          (4 -> Cell.dead),
          (5 -> Cell.dead)
        )

        run_tests(Cell.living, tests)
      }

      describe("when cell is dead,") {
        val tests = List(
          (0 -> Cell.dead),
          (1 -> Cell.dead),
          (2 -> Cell.dead),
          (3 -> Cell.living),
          (4 -> Cell.dead),
          (5 -> Cell.dead)
        )

        run_tests(Cell.dead, tests)
      }
    }
  }

  describe("Cell Object") {
    describe("get(Boolean)") {
      it("returns Cell.living if true") {
        Cell.get(true) should be (Cell.living)
      }

      it("returns Cell.dead if false") {
        Cell.get(false) should be (Cell.dead)
      }
    }

    describe("get(Char)") {
      it("returns the dead cell if char is -") {
        Cell.get('-') should be (Cell.dead)
      }

      it("returns the alive cell if char is +") {
        Cell.get('+') should be (Cell.living)
      }
    }

    describe("living") {
      it("returns singleton living cell") {
        val cell = Cell.living
        cell should be (Cell.living)
        cell should === (Cell(true))
      }
    }

    describe("dead") {
      it("returns singleton dead cell") {
        val cell = Cell.dead
        cell should be (Cell.dead)
        cell should === (Cell(false))
      }
    }
  }

  describe("Grid Class") {
    describe("toString") {
      it("returns a string representation") {
        val str = "--+\n-+-\n+--"
        Grid.build(str).fold(x => x, _.toString) should === (str)
      }
    }
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
                Vector(Cell.dead, Cell.dead, Cell.living),
                Vector(Cell.dead, Cell.living, Cell.dead),
                Vector(Cell.living, Cell.dead, Cell.dead)
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
