package info.ditrapani.gameoflife

import config.{BoardSource, Config}

import scala.util.{Try, Success, Failure}

object BoardLoader {

  def getBoardStr(board_source: BoardSource.Source): Either[String, String] =
    board_source match {
      case BoardSource.BuiltIn(index) =>
        val name = Config.boards(index)
        val input_stream = getClass.getResourceAsStream(s"/$name.txt")
        Right(scala.io.Source.fromInputStream(input_stream).mkString)
      case BoardSource.File(path) =>
        Try(scala.io.Source.fromFile(path).mkString) match {
          case Failure(exception) => Left(exception.toString())
          case Success(board_str) => Right(board_str)
        }
    }
}
