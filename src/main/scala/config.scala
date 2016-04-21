package info.ditrapani.gameoflife

import scala.util.{Try, Success, Failure}
import scala.util.matching.Regex.Match

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
  dead_color: (Int, Int, Int),
  bg_color: (Int, Int, Int)
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
      (90, 100, 130),
      (150, 170, 200)
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
      case "alive-color" => handleAliveColor(value, config)
      case "dead-color" => handleDeadColor(value, config)
      case "bg-color" => handleBgColor(value, config)
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

  def handleAliveColor(value: String, config: Config): IfConfig = {
    handleColor(value, "alive") match {
      case Right((r, g, b)) => Right(config.copy(alive_color = (r, g, b)))
      case Left(s) => Left(s)
    }
  }

  def handleDeadColor(value: String, config: Config): IfConfig = {
    handleColor(value, "dead") match {
      case Right((r, g, b)) => Right(config.copy(dead_color = (r, g, b)))
      case Left(s) => Left(s)
    }
  }

  def handleBgColor(value: String, config: Config): IfConfig = {
    handleColor(value, "bg") match {
      case Right((r, g, b)) => Right(config.copy(bg_color = (r, g, b)))
      case Left(s) => Left(s)
    }
  }

  def handleColor(value: String, color_type: String): Either[String, (Int, Int, Int)] = {
    val left = Left(s"--$color_type-color must be Int,Int,Int between 0-255")
    def processMatch(m: Match): Either[String, (Int, Int, Int)] = {
      val r = m.group(1)
      val g = m.group(2)
      val b = m.group(3)
      val either_r = parseInt(r, 0, 255)
      val either_g = parseInt(g, 0, 255)
      val either_b = parseInt(b, 0, 255)
      (either_r.isRight && either_g.isRight && either_b.isRight) match {
        case true => Right((r.toInt, g.toInt, b.toInt))
        case false => left
      }
    }

    val pattern = "^(\\d{1,3}),(\\d{1,3}),(\\d{1,3})$".r
    pattern.findFirstMatchIn(value) match {
      case Some(m) => processMatch(m)
      case None => left
    }
  }

  def parseInt(value: String, lower: Int, upper: Int, prefix: String = ""): Either[String, Int] = {
    val left = Left(prefix + s", must be an integer between $lower and $upper")
    def on_success(num: Int): Either[String, Int] = {
      (num < lower || num > upper) match {
        case true => left
        case false => Right(num)
      }
    }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => on_success(num)
    }
  }
}
