package info.ditrapani.gameoflife

import config.{BuiltIn, File}

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class BoardLoaderSpec extends Spec {
  val blinkerString = "-----\n--+--\n--+--\n--+--\n-----\n"

  describe("getBoardStr") {
    describe("when given a BuiltIn BoardSource") {
      it("loads the built-in board") {
        BoardLoader
          .getBoardStr(BuiltIn(2))
          .shouldBe(Right(blinkerString))
      }
    }

    describe("when given a File BoardSource") {
      describe("and the file does not exist") {
        it("returns a Left(exception)") {
          BoardLoader
            .getBoardStr(File("src/main/resources/"))
            .shouldBe(Left("java.io.FileNotFoundException: src/main/resources (Is a directory)"))
        }
      }

      describe("and the file exist") {
        it("loads the board from file") {
          BoardLoader
            .getBoardStr(File("src/main/resources/blinker.txt"))
            .shouldBe(Right(blinkerString))
        }
      }
    }
  }
}
