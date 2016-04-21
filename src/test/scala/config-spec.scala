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
    val i = (Config.boards.indexOf("blinker") + 1).toString

    describe("load") {
      describe("--help") {
        it("returns Left") {
          Config.load(List("--help"), Map()) should === (
            Left("Printing help text...")
          )
        }
      }

      it("returns Left if unknown parameters found in help_params") {
        Config.load(List("foo"), Map()) should === (
          Left("Unknown command line parameter 'foo'")
        )
      }

      it("returns Left if unknown parameters found in params") {
        Config.load(List(), Map("b" -> i, "a" -> "")) should === (
          Left("Unknown command line parameter '--a'")
        )
      }

      describe("--b") {
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
          Config.load(List(), Map("b" -> i)) should === (
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

        it(
          "returns Left if --b is specified with integer > " +
          s"${Config.board_count}"
        ) {
          val end_plus_one = (Config.board_count + 1).toString
          Config.load(List(), Map("b" -> end_plus_one)) should === (
            Left(
              "Invalid value for --b; must be an integer between 1 and " +
              s"${Config.board_count}"
            )
          )
        }
      }

      describe("--b & --f") {
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
          Config.load(List(), Map("f" -> f, "b" -> i)) should === (
            Left("Cannot define both --b and --f as board source; pick one")
          )
        }
      }

      it("returns a Right if --b and --t are set") {
        Config.load(List(), Map("b" -> i, "t" -> "250")) should === (
          Right(
            Config.emptyConfig.copy(
              board_source = BoardSource.BuiltIn,
              board_str = "-----\n--+--\n--+--\n--+--\n-----\n",
              time_delta = 250
            )
          )
        )
      }

      describe("--f") {
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
      }

      describe("--t") {
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
      }

      it("returns a Right if --m and --w are set") {
        val params = Map("b" -> i, "m" -> "0", "w" -> "1")
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
        Config.load(List(), Map("b" -> i, "m" -> "-1")) should === (
          Left("--m must be a non-negative integer number")
        )
      }

      it("returns a Left if --m is not an integer") {
        Config.load(List(), Map("b" -> i, "m" -> "foo")) should === (
          Left("--m must be a non-negative integer number")
        )
      }

      it("returns a Left if --w is not positive") {
        Config.load(List(), Map("b" -> i, "w" -> "0")) should === (
          Left("--w must be a positive integer number")
        )
      }

      it("returns a Left if --w is negative") {
        Config.load(List(), Map("b" -> i, "w" -> "-1")) should === (
          Left("--w must be a positive integer number")
        )
      }

      it("returns a Left if --w is not an integer") {
        Config.load(List(), Map("b" -> i, "w" -> "foo")) should === (
          Left("--w must be a positive integer number")
        )
      }

      describe("--alive-color") {
        it("returns Left if bad format") {
          val params = Map("b" -> i, "alive-color" -> "0,100,300,")
          Config.load(List(), params) should === (
            Left("--alive-color must be Int,Int,Int between 0-255")
          )
        }

        it("returns Right if good format") {
          val params = Map("b" -> i, "alive-color" -> "0,100,255")
          Config.load(List(), params) should === (
            Right(
              Config.emptyConfig.copy(
                board_source = BoardSource.BuiltIn,
                board_str = "-----\n--+--\n--+--\n--+--\n-----\n",
                alive_color = (0, 100, 255)
              )
            )
          )
        }
      }
    }

    describe("handleColor") {
      val left: Either[String, (Int, Int, Int)] = Left("--bg-color must be Int,Int,Int between 0-255")

      it("returns Right((Int, Int, Int)) for 1,10,100") {
        Config.handleColor("1,10,100", "bg") should === (Right((1, 10, 100)))
        }

      it("returns Left if tuple has leading garbage") {
        Config.handleColor("hi1,10,100", "bg") should === (left)
      }

      it("returns Left if tuple has extra comma") {
        Config.handleColor("1,10,100,", "bg") should === (left)
      }

      it("returns Left if missing comma") {
        Config.handleColor("1,10 100", "bg") should === (left)
      }

      it("returns Left if not tuple") {
        Config.handleColor("foo", "bg") should === (left)
      }

      it("returns Left if there is a non-number") {
        Config.handleColor("1,2,foo", "bg") should === (left)
      }

      it("returns Left if not all ints") {
        Config.handleColor("1,2,1.5", "bg") should === (left)
      }

      it("returns Left if less than 0") {
        Config.handleColor("1,2,-1", "bg") should === (left)
      }

      it("returns Left if greater than 255") {
        Config.handleColor("1,2,256", "bg") should === (left)
      }

      it("returns Right even if leading zero") {
        Config.handleColor("1,2,06", "bg") should === (Right(1,2,6))
      }
    }

    describe("parseInt") {
      it("returns Right(Int) if string is int within bounds") {
        Config.parseInt("5", 4, 6) should === (Right(5))
      }

      it("can pass an optional prefix") {
        Config.parseInt("5", 4, 6, "foo") should === (Right(5))
      }

      it("returns Left if string is not int") {
        Config.parseInt("foo", 4, 6, "Prefix") should === (
          Left("Prefix, must be an integer between 4 and 6")
        )
      }

      it("returns Left if string int below bound") {
        Config.parseInt("9", 10, 20, "Prefix") should === (
          Left("Prefix, must be an integer between 10 and 20")
        )
      }

      it("returns Left if string int above bound") {
        Config.parseInt("21", 10, 20) should === (
          Left(", must be an integer between 10 and 20")
        )
      }
    }
  }
}
