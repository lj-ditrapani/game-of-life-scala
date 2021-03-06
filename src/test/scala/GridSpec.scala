package info.ditrapani.gameoflife

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class GridSpec extends Spec {
  describe("Grid Class") {
    describe("toString") {
      it("returns a string representation") {
        val str = "--+\n-+-\n+--"
        Grid.build(str).fold(x => x, _.toString) should ===(str)
      }
    }

    describe("countAliveNeighbors") {
      val tests: List[(Int, Int, Int)] = List(
        (1, 1, 5),
        (0, 0, 4),
        (4, 4, 4),
        (2, 1, 3),
        (3, 4, 3)
      )

      val str = """++--+
                  |--+--
                  |-++--
                  |+----
                  |+---+""".stripMargin

      for (test <- tests) {
        val (row, column, count) = test
        val either = Grid.build(str)
        val result = either.fold(_ => -1, _.countAliveNeighbors(row, column))
        it(s"returns $count at ($row, $column) for given grid") {
          result should be(count)
        }
      }
    }

    describe("next") {
      it("computes next board 1 cell dies") {
        val str1 = "+--\n---\n---"
        val str2 = "---\n---\n---"
        Grid.build(str1).fold(x => x, _.next.toString) should ===(str2)
      }

      it("computes next board fills board") {
        val str1 = "-+-\n-+-\n-+-"
        val str2 = "+++\n+++\n+++"
        Grid.build(str1).fold(x => x, _.next.toString) should ===(str2)
      }

      it("computes next board for cross pulsar") {
        val str1 = """-----
                     |--+--
                     |--+--
                     |--+--
                     |-----""".stripMargin

        val str2 = """-----
                     |-----
                     |-+++-
                     |-----
                     |-----""".stripMargin

        Grid.build(str1).fold(x => x, _.next.toString) should ===(str2)
        Grid.build(str1).fold(x => x, _.next.next.toString) should ===(str1)
      }

      it("computes next board for a toad pulsar") {
        val str1 = """------
                     |------
                     |--+++-
                     |-+++--
                     |------
                     |------""".stripMargin

        val str2 = """------
                     |---+--
                     |-+--+-
                     |-+--+-
                     |--+---
                     |------""".stripMargin

        Grid.build(str1).fold(x => x, _.next.toString) should ===(str2)
        Grid.build(str1).fold(x => x, _.next.next.toString) should ===(str1)
      }

      it("computes next board for a boat still life") {
        val str1 = """-----
                     |-++--
                     |-+-+-
                     |--+--
                     |-----""".stripMargin

        Grid.build(str1).fold(x => x, _.next.toString) should ===(str1)
      }
    }
  }

  describe("Grid Object") {
    describe("build") {
      it("returns Left if rows < 3") {
        val str = "--++-\n+-+-+"
        Grid.build(str) should ===(Left("Board must be at least 3 x 3"))
      }

      it("returns Left if columns < 3") {
        val str = "--\n+-\n++"
        Grid.build(str) should ===(Left("Board must be at least 3 x 3"))
      }

      it("returns Left if line lengths don't match") {
        val str = "--+\n+-\n+++"
        Grid.build(str) should ===(Left("Board line lengths don't match"))
      }

      it("returns Left if any char is not + or -") {
        val str = "--+\n+?-\n+++"
        Grid.build(str) should ===(
          Left("Board must contain only + and - characters")
        )
      }

      it("returns Right(Grid) if the str is legal") {
        val str = "--+\n-+-\n+--"
        Grid.build(str) should ===(
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

    describe("lineLengthsMatch") {
      it("returns true if all lines are the same length") {
        val lines = Vector("--++-", "+-+-+", "++--+")
        Grid.lineLengthsMatch(lines) should be(true)
      }

      it("returns false if the second line differs in length") {
        val lines = Vector("+-+-+", "+-+-", "+-+-")
        Grid.lineLengthsMatch(lines) should be(false)
      }

      it("returns false the last line differs in length") {
        val lines = Vector("+-+-+", "+-+-+", "+-+-")
        Grid.lineLengthsMatch(lines) should be(false)
      }
    }

    describe("onlyPlusesAndDashes") {
      it("returns true if all lines only have +'s & -'s") {
        val lines = Vector("--++-", "+-+-+", "++--+")
        Grid.onlyPlusesAndDashes(lines) should be(true)
      }

      it("returns false if any line contains something else") {
        val lines = Vector("--++-", "+-+-+", "++|-+")
        Grid.onlyPlusesAndDashes(lines) should be(false)
      }
    }
  }
}
