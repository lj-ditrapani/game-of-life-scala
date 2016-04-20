package info.ditrapani.gameoflife

import org.scalatest.{FunSpec, Matchers}

class ConfigSpec extends FunSpec with Matchers {
  describe("Config Class") {
    it("has instance values") {
      val config = Config.emptyConfig
      config.board_source should be (BoardSource.UnSet)
    }
  }

  describe("Config Object") {
    describe("load") {
      it("returns Left if --help is found") {
        Config.load(List("--help"), Map()) should === (
          Left("Printing help text...")
        )
      }

      it("returns Left if unknown parameters found in help_params") {
        Config.load(List("foo"), Map()) should === (
          Left("Unknown command line parameter 'foo'")
        )
      }

      it("returns Left if unknown parameters found in params") {
        Config.load(List(), Map("b" -> "3", "a" -> "")) should === (
          Left("Unknown command line parameter '--a'")
        )
      }

      it("returns Left if --b is specified with empty string") {
        Config.load(List(), Map("b" -> "")) should === (
          Left(
            "Invalid value for --b; must be an integer between 1 and " +
            s"${Config.board_count}"
          )
        )
      }

      it("returns Left if --b is specified without integer") {
        Config.load(List(), Map("b" -> "foo")) should === (
          Left(
            "Invalid value for --b; must be an integer between 1 and " +
            s"${Config.board_count}"
          )
        )
      }

      it("returns Right if --b is specified with integer") {
        Config.load(List(), Map("b" -> "3")) should === (
          Right(
            Config.emptyConfig.copy(
              board_source = BoardSource.BuiltIn,
              board_str = "-----\n--+--\n--+--\n--+--\n-----\n"
            )
          )
        )
      }

      it("returns Left if --b is specified with integer < 1") {
        Config.load(List(), Map("b" -> "0")) should === (
          Left(
            "Invalid value for --b; must be an integer between 1 and " +
            s"${Config.board_count}"
          )
        )
      }

      it("returns Left if --b is specified with integer > 9") {
        val i = (Config.board_count + 1).toString
        Config.load(List(), Map("b" -> i)) should === (
          Left(
            "Invalid value for --b; must be an integer between 1 and " +
            s"${Config.board_count}"
          )
        )
      }

      it("returns Left if neither --b nor --f are set") {
        Config.load(List(), Map()) should === (
          Left("Must define either --b or --f as board source")
        )
      }

      it("returns Left if both --b and --f are set") {
        Config.load(List(), Map("b" -> "1", "f" -> "x")) should === (
          Left("Cannot define both --b and --f as board source; pick one")
        )
      }

      it("returns Left if both --f and --b are set") {
        val f = "src/main/resources/blinker.txt"
        Config.load(List(), Map("f" -> f, "b" -> "3")) should === (
          Left("Cannot define both --b and --f as board source; pick one")
        )
      }

      it("returns a Right if --b and --t are set") {
        Config.load(List(), Map("b" -> "3", "t" -> "250")) should === (
          Right(
            Config.emptyConfig.copy(
              board_source = BoardSource.BuiltIn,
              board_str = "-----\n--+--\n--+--\n--+--\n-----\n",
              time_delta = 250
            )
          )
        )
      }

      it("returns a Right if --f is set") {
        val f = "src/main/resources/blinker.txt"
        Config.load(List(), Map("f" -> f)) should === (
          Right(
            Config.emptyConfig.copy(
              board_source = BoardSource.File,
              board_str = "-----\n--+--\n--+--\n--+--\n-----\n"
            )
          )
        )
      }

      it("returns a Left if --f is not a file") {
        val f = "src/main/resources/"
        val msg = """java.io.FileNotFoundException: src/main/resources
                    |(Is a directory)""".stripMargin.replaceAll("\n", " ")
        Config.load(List(), Map("f" -> f)) should === (Left(msg))
      }

      it("returns a Left if --t is not a number") {
        Config.load(List(), Map("b" -> "1", "t" -> "foo")) should === (
          Left("--t must be a positive integer number")
        )
      }

      it("returns a Left if --t is 0") {
        Config.load(List(), Map("b" -> "1", "t" -> "0")) should === (
          Left("--t must be a positive integer number")
        )
      }

      it("returns a Left if --t is negative") {
        Config.load(List(), Map("b" -> "1", "t" -> "-1")) should === (
          Left("--t must be a positive integer number")
        )
      }

      it("returns a Right if --m and --w are set") {
        val params = Map("b" -> "3", "m" -> "0", "w" -> "1")
        Config.load(List(), params) should === (
          Right(
            Config.emptyConfig.copy(
              board_source = BoardSource.BuiltIn,
              board_str = "-----\n--+--\n--+--\n--+--\n-----\n",
              margin = 0,
              width = 1
            )
          )
        )
      }

      it("returns a Left if --m is negative") {
        Config.load(List(), Map("b" -> "3", "m" -> "-1")) should === (
          Left("--m must be a non-negative integer number")
        )
      }

      it("returns a Left if --m is not an integer") {
        Config.load(List(), Map("b" -> "3", "m" -> "foo")) should === (
          Left("--m must be a non-negative integer number")
        )
      }

      it("returns a Left if --w is not positive") {
        Config.load(List(), Map("b" -> "3", "w" -> "0")) should === (
          Left("--w must be a positive integer number")
        )
      }

      it("returns a Left if --w is negative") {
        Config.load(List(), Map("b" -> "3", "w" -> "-1")) should === (
          Left("--w must be a positive integer number")
        )
      }

      it("returns a Left if --w is not an integer") {
        Config.load(List(), Map("b" -> "3", "w" -> "foo")) should === (
          Left("--w must be a positive integer number")
        )
      }
    }
  }
}
