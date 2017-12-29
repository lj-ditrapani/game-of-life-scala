package info.ditrapani.gameoflife

import config.BoardSource

class BoardLoaderImplSpec extends Spec {
  val blinker_string = "-----\n--+--\n--+--\n--+--\n-----\n"

  describe("getBoardStr") {
    describe("when given a BuiltIn BoardSource") {
      it("loads the built-in board") {
        BoardLoaderImpl
          .getBoardStr(BoardSource.BuiltIn(2))
          .shouldBe(Right(blinker_string))
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
            .shouldBe(Right(blinker_string))
        }
      }
    }
  }
}
