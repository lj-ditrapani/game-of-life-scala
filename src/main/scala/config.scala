package info.ditrapani.gameoflife

import scala.util.{Try, Success, Failure}

object BoardSource extends Enumeration {
  type Source = Value
  val BuiltIn, File, UnSet = Value
}

case class Config(
  board_source: BoardSource.Source,
  board_str: String,
  time_delta: Long,
  margin: Int,
  width: Int,
  alive_color: (Int, Int, Int),
  dead_color: (Int, Int, Int)
)

object Config {
  type IfConfig = Either[String, Config]

  val boards = Vector(
    "acorn",
    "blinkers",
    "blinker",
    "block-laying-switch-engine2",
    "diehard",
    "glider",
    "gosper-glider-gun",
    "pentadecathlon",
    "r-pentomino"
  )
  val board_count = Config.boards.size

  val emptyConfig: Config = {
    Config(
      BoardSource.UnSet, "", 500L, 4, 16,
      (200, 220, 255),
      (100, 120, 150)
    )
  }

  def load(help_params: Seq[String], params: Map[String,String]): IfConfig = {
    if (help_params.exists(p => p == "--help")) {
      Left("Printing help text...")
    } else if (!help_params.isEmpty) {
      Left(s"Unknown command line parameter '${help_params.head}'")
    } else {
      val if_config1: IfConfig = Right(Config.emptyConfig)
      val if_config2 = params.foldLeft(if_config1) { (if_config, kv) =>
        if_config.right.flatMap { addParams(kv, _) }
      }
      if_config2.right.flatMap { (config) =>
        config.board_source == BoardSource.UnSet match {
          case true => Left("Must define either --b or --f as board source")
          case false => Right(config)
        }
      }
    }
  }

  def addParams(kv: (String, String), config: Config): IfConfig = {
    val (flag, value) = kv
    flag match {
      case "b" => handleBuiltIn(value, config)
      case "f" => handleFile(value, config)
      case "t" => handleTimeDelta(value, config)
      case "m" => handleMargin(value, config)
      case "w" => handleWidth(value, config)
      case "alive-color" => Left("--alive-color not yet implemented")
      case "dead-color" => Left("--dead-color not yet implemented")
      case _ => Left(s"Unknown command line parameter '--${flag}'")
    }
  }

  def handleTimeDelta(value: String, config: Config): IfConfig = {
    val left = Left("--t must be a positive integer number")
    def onSuccess(num: Long): IfConfig = {
      if (num < 1L) left else Right(config.copy(time_delta = num))
    }

    Try(value.toLong) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }

  def handleBuiltIn(value: String, config: Config): IfConfig = {
    val left = Left(
      "Invalid value for --b; must be an integer between 1 and 9"
    )

    def onSuccess(num: Int): IfConfig = {
      if (num < 1 || num > board_count) {
        left
      } else {
        val name = boards(num - 1)
        val input_stream = getClass.getResourceAsStream(s"/$name.txt")
        val board_str = scala.io.Source.fromInputStream(input_stream).mkString
        Right(
          config.copy(
            board_source = BoardSource.BuiltIn,
            board_str = board_str
          )
        )
      }
    }

    (config.board_source == BoardSource.File) match {
      case true =>
        Left("Cannot define both --b and --f as board source; pick one")
      case false => {
        Try(value.toInt) match {
          case Failure(_) => left
          case Success(num) => onSuccess(num)
        }
      }
    }
  }

  def handleFile(value: String, config: Config): IfConfig = {
    if (config.board_source == BoardSource.BuiltIn) {
      Left("Cannot define both --b and --f as board source; pick one")
    } else {
      Try(scala.io.Source.fromFile(value).mkString) match {
        case Failure(exception) =>
          Left(exception.toString())
        case Success(board_str) => {
          Right(
            config.copy(
              board_source = BoardSource.File,
              board_str = board_str
            )
          )
        }
      }
    }
  }

  def handleMargin(value: String, config: Config): IfConfig = {
    val left = Left("--m must be a non-negative integer number")
    def onSuccess(num: Int): IfConfig = {
      if (num < 0) left else Right(config.copy(margin = num))
    }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }

  def handleWidth(value: String, config: Config): IfConfig = {
    val left = Left("--w must be a positive integer number")
    def onSuccess(num: Int): IfConfig = {
      if (num < 1) left else Right(config.copy(width = num))
    }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }
}
