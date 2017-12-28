package info.ditrapani.gameoflife

class CellSpec extends Spec {
  describe("Cell Class") {
    describe("alive") {
      it("returns false if dead") {
        Cell.dead.alive shouldBe false
      }

      it("returns true if alive") {
        Cell.living.alive shouldBe true
      }
    }

    describe("toChar") {
      it("returns + if alive") {
        Cell.living.toChar shouldBe '+'
      }

      it("it returns - if dead") {
        Cell.dead.toChar shouldBe '-'
      }
    }

    describe("next") {
      def run_tests(base_cell: Cell, tests: List[(Int, Cell)]): Unit = {
        for (test <- tests) {
          val (count, expected_cell) = test
          val state = if (expected_cell.alive) "living" else "dead"
          it(s"returns the $state Cell if neighbor_count == $count") {
            base_cell.next(count) should be(expected_cell)
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
        Cell.get(true) should be(Cell.living)
      }

      it("returns Cell.dead if false") {
        Cell.get(false) should be(Cell.dead)
      }
    }

    describe("get(Char)") {
      it("returns the dead cell if char is -") {
        Cell.get('-') should be(Cell.dead)
      }

      it("returns the alive cell if char is +") {
        Cell.get('+') should be(Cell.living)
      }
    }

    describe("living") {
      it("returns singleton living cell") {
        val cell = Cell.living
        cell shouldBe Cell.living
        cell should ===(Cell(true))
      }
    }

    describe("dead") {
      it("returns singleton dead cell") {
        val cell = Cell.dead
        cell shouldBe Cell.dead
        cell should ===(Cell(false))
      }
    }
  }
}
