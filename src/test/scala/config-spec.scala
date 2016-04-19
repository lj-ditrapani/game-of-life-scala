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
        Config.load(List(), Map("a" -> "")) should === (
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
        Config.load(List(), Map("b" -> "9")) should === (
          Right(Config(BoardSource.BuiltIn, "", 500))
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
        Config.load(List(), Map("b" -> "x", "f" -> "x")) should === (
          Left("Cannot define both --b and --f as board source; pick one")
        )
      }

      it("returns a Right if --b and --t are set") {
        Config.load(List(), Map("b" -> "1", "t" -> "250")) should === (
          Right(Config(BoardSource.BuiltIn, "", 500))
        )
      }
    }
  }
}
