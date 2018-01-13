package info.ditrapani.gameoflife

import config.BoardSource

@SuppressWarnings(Array("org.wartremover.warts.Nothing"))
class BoardLoaderImplSpec extends Spec {
  val blinkerString = "-----\n--+--\n--+--\n--+--\n-----\n"

  describe("getBoardStr") {
    describe("when given a BuiltIn BoardSource") {
      it("loads the built-in board") {
        BoardLoaderImpl
          .getBoardStr(BoardSource.BuiltIn(2))
          .shouldBe(Right(blinkerString))
      }
    }

    describe("when given a File BoardSource") {
      describe("and the file does not exist") {
        it("returns a Left(exception)") {
          BoardLoaderImpl
            .getBoardStr(BoardSource.File("src/main/resources/"))
            .shouldBe(Left("java.io.FileNotFoundException: src/main/resources (Is a directory)"))
        }
      }

      describe("and the file exist") {
        it("loads the board from file") {
          BoardLoaderImpl
            .getBoardStr(BoardSource.File("src/main/resources/blinker.txt"))
            .shouldBe(Right(blinkerString))
        }
      }
    }
  }
}
