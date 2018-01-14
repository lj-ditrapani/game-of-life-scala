package info.ditrapani.gameoflife

import config.{BoardSource, BuiltIn, Config, File}

import scala.util.{Try, Success, Failure}

trait BoardLoader {
  def getBoardStr(boardSource: BoardSource): Either[String, String]
}

object BoardLoaderImpl extends BoardLoader {

  def getBoardStr(boardSource: BoardSource): Either[String, String] =
    boardSource match {
      case BuiltIn(index) =>
        val name = Config.boards(index)
        val input_stream = getClass.getResourceAsStream(s"/$name.txt")
        Right[String, String](scala.io.Source.fromInputStream(input_stream).mkString)
      case File(path) =>
        Try(scala.io.Source.fromFile(path).mkString) match {
          case Failure(exception) => Left[String, String](exception.toString())
          case Success(board_str) => Right[String, String](board_str)
        }
    }
}
