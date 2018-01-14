package info.ditrapani.gameoflife.config

import info.ditrapani.gameoflife.Spec

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class ConfigSpec extends Spec {
  describe("Config Class") {
    it("has instance values") {
      val source = BuiltIn(1)
      val config = Config.defaultConfig(source)
      config.boardSource shouldBe source
    }
  }

  describe("Config Object") {
    val index = Config.boards.indexOf("blinker")
    val num = (index + 1).toString

    describe("parse") {
      describe("--help") {
        it("returns Help") {
          Config.parse(List("--help"), Map()) should ===(
            Help
          )
        }
      }

      it("returns Error if unknown parameters found in helpParams") {
        Config.parse(List("foo"), Map()) should ===(
          Error("Unknown command line parameter in List(foo)")
        )
      }

      it("returns Error if unknown parameters found in params") {
        Config.parse(List(), Map("b" -> num, "a" -> "")) should ===(
          Error("Unknown command line parameter '--a'")
        )
      }

      describe("--b") {
        it("returns Error if --b is specified with empty string") {
          Config.parse(List(), Map("b" -> "")) should ===(
            Error(
              "Invalid value for --b, must be an integer between 1 and " +
                s"${Config.boardCount}"
            )
          )
        }

        it("returns Error if --b is specified without integer") {
          Config.parse(List(), Map("b" -> "foo")) should ===(
            Error(
              "Invalid value for --b, must be an integer between 1 and " +
                s"${Config.boardCount}"
            )
          )
        }

        it("returns Go if --b is specified with integer") {
          Config.parse(List(), Map("b" -> num)) should ===(
            Go(
              Config.defaultConfig(BuiltIn(index))
            )
          )
        }

        it("returns Error if --b is specified with integer < 1") {
          Config.parse(List(), Map("b" -> "0")) should ===(
            Error(
              "Invalid value for --b, must be an integer between 1 and " +
                s"${Config.boardCount}"
            )
          )
        }

        it(
          "returns Error if --b is specified with integer > " +
            s"${Config.boardCount}"
        ) {
          val endPlusOne = (Config.boardCount + 1).toString
          Config.parse(List(), Map("b" -> endPlusOne)) should ===(
            Error(
              "Invalid value for --b, must be an integer between 1 and " +
                s"${Config.boardCount}"
            )
          )
        }
      }

      describe("--b & --f") {
        it("returns Error if neither --b nor --f are set") {
          Config.parse(List(), Map()) should ===(
            Error("Must define either --b or --f as board source")
          )
        }

        it("returns Error if both --b and --f are set") {
          Config.parse(List(), Map("b" -> "1", "f" -> "x")) should ===(
            Error("Cannot define both --b and --f as board source; pick one")
          )
        }

        it("returns Error if both --f and --b are set") {
          val f = "src/main/resources/blinker.txt"
          Config.parse(List(), Map("f" -> f, "b" -> num)) should ===(
            Error("Cannot define both --b and --f as board source; pick one")
          )
        }
      }

      it("returns a Go if --b and --t are set") {
        Config.parse(List(), Map("b" -> num, "t" -> "250")) should ===(
          Go(
            Config
              .defaultConfig(BuiltIn(index))
              .copy(timeDelta = 250)
          )
        )
      }

      describe("--f") {
        it("returns a Go if --f is set") {
          val f = "src/main/resources/blinker.txt"
          Config.parse(List(), Map("f" -> f)) should ===(
            Go(Config.defaultConfig(File(f)))
          )
        }
      }

      describe("--t") {
        it("returns a Error if --t is not a number") {
          Config.parse(List(), Map("b" -> "1", "t" -> "foo")) should ===(
            Error("--t must be an integer between 1 and 4096")
          )
        }

        it("returns a Error if --t is 0") {
          Config.parse(List(), Map("b" -> "1", "t" -> "0")) should ===(
            Error("--t must be an integer between 1 and 4096")
          )
        }

        it("returns a Error if --t is negative") {
          Config.parse(List(), Map("b" -> "1", "t" -> "-1")) should ===(
            Error("--t must be an integer between 1 and 4096")
          )
        }
      }

      it("returns a Go if --m and --w are set") {
        val params = Map("b" -> num, "m" -> "0", "w" -> "1")
        Config.parse(List(), params) should ===(
          Go(
            Config
              .defaultConfig(BuiltIn(index))
              .copy(
                margin = 0,
                width = 1
              )
          )
        )
      }

      describe("--m") {
        it("returns a Error if --m is negative") {
          Config.parse(List(), Map("b" -> num, "m" -> "-1")) should ===(
            Error("--m must be an integer between 0 and 4096")
          )
        }

        it("returns a Error if --m is not an integer") {
          Config.parse(List(), Map("b" -> num, "m" -> "foo")) should ===(
            Error("--m must be an integer between 0 and 4096")
          )
        }
      }

      describe("--w") {
        it("returns a Error if --w is not positive") {
          Config.parse(List(), Map("b" -> num, "w" -> "0")) should ===(
            Error("--w must be an integer between 1 and 4096")
          )
        }

        it("returns a Error if --w is negative") {
          Config.parse(List(), Map("b" -> num, "w" -> "-1")) should ===(
            Error("--w must be an integer between 1 and 4096")
          )
        }

        it("returns a Error if --w is not an integer") {
          Config.parse(List(), Map("b" -> num, "w" -> "foo")) should ===(
            Error("--w must be an integer between 1 and 4096")
          )
        }
      }

      describe("--alive-color") {
        it("returns Error if bad format") {
          val params = Map("b" -> num, "alive-color" -> "0,100,300,")
          Config.parse(List(), params) should ===(
            Error("--alive-color must be Int,Int,Int between 0-255")
          )
        }

        it("returns Go if good format") {
          val params = Map("b" -> num, "alive-color" -> "0,100,255")
          Config.parse(List(), params) should ===(
            Go(
              Config
                .defaultConfig(BuiltIn(index))
                .copy(aliveColor = (0, 100, 255))
            )
          )
        }
      }

      describe("--dead-color") {
        it("returns Error if bad format") {
          val params = Map("b" -> num, "dead-color" -> "0,100,300,")
          Config.parse(List(), params) should ===(
            Error("--dead-color must be Int,Int,Int between 0-255")
          )
        }

        it("returns Go if good format") {
          val params = Map("b" -> num, "dead-color" -> "0,100,255")
          Config.parse(List(), params) should ===(
            Go(
              Config
                .defaultConfig(BuiltIn(index))
                .copy(deadColor = (0, 100, 255))
            )
          )
        }
      }

      describe("--bg-color") {
        it("returns Error if bad format") {
          val params = Map("b" -> num, "bg-color" -> "0,100,300,")
          Config.parse(List(), params) should ===(
            Error("--bg-color must be Int,Int,Int between 0-255")
          )
        }

        it("returns Go if good format") {
          val params = Map("b" -> num, "bg-color" -> "0,100,255")
          Config.parse(List(), params) should ===(
            Go(
              Config
                .defaultConfig(BuiltIn(index))
                .copy(bgColor = (0, 100, 255))
            )
          )
        }
      }
    }

    describe("handleColor") {
      val left: Either[String, (Int, Int, Int)] = Left(
        "--bg-color must be Int,Int,Int between 0-255"
      )

      val leftTests: List[(String, String)] = List(
        ("tuple has leading garbage", "hi1,10,100"),
        ("tuple has extra comma", "1,10,100,"),
        ("missing comma", "1,10 100"),
        ("not tuple", "foo"),
        ("there is a non-number", "1,2,foo"),
        ("not all ints", "1,2,1.5"),
        ("any values less than 0", "1,2,-1"),
        ("any values greater than 255", "1,2,256")
      )

      for ((msg, str) <- leftTests) {
        it(s"returns Left if ${msg}") {
          Config.handleColor(str, "bg") should be(left)
        }
      }

      it("returns Right((Int, Int, Int)) for 1,10,100") {
        Config.handleColor("1,10,100", "bg") should ===(Right((1, 10, 100)))
      }

      it("returns Right even if leading zero") {
        Config.handleColor("1,2,06", "bg") should ===(Right((1, 2, 6)))
      }
    }

    describe("parseInt") {
      it("returns Right(Int) if string is int within bounds") {
        Config.parseInt("5", 4, 6, "") should ===(Right(5))
      }

      it("can pass an optional prefix") {
        Config.parseInt("5", 4, 6, "foo") should ===(Right(5))
      }

      it("returns Left if string is not int") {
        Config.parseInt("foo", 4, 6, "Prefix") should ===(
          Left("Prefix must be an integer between 4 and 6")
        )
      }

      it("returns Left if string int below bound") {
        Config.parseInt("9", 10, 20, "Prefix") should ===(
          Left("Prefix must be an integer between 10 and 20")
        )
      }

      it("returns Left if string int above bound") {
        Config.parseInt("21", 10, 20, "") should ===(
          Left(" must be an integer between 10 and 20")
        )
      }
    }
  }
}
