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
  time_delta: Int,
  margin: Int,
  width: Int,
  alive_color: (Int, Int, Int),
  dead_color: (Int, Int, Int),
  bg_color: (Int, Int, Int)
)

object Config {
  type IfConfig = Either[String, Config]
  type IfInt = Either[String, Int]

  val boards = Vector(
    "acorn",
    "blinkers",
    "blinker",
    "block-laying-switch-engine2",
    "box",
    "diehard",
    "glider",
    "gosper-glider-gun",
    "pentadecathlon",
    "r-pentomino"
  )
  val board_count = Config.boards.size

  val emptyConfig: Config = {
    Config(
      BoardSource.UnSet, "", 500, 4, 16,
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

  def handleBuiltIn(value: String, config: Config): IfConfig = {
    def onSuccess(num: Int): Config = {
      val name = boards(num - 1)
      val input_stream = getClass.getResourceAsStream(s"/$name.txt")
      val board_str = scala.io.Source.fromInputStream(input_stream).mkString
      config.copy(
        board_source = BoardSource.BuiltIn,
        board_str = board_str
      )
    }

    (config.board_source == BoardSource.File) match {
      case true =>
        Left("Cannot define both --b and --f as board source; pick one")
      case false => {
        parseIntAndDo(value, 1, board_count, "Invalid value for --b,") {
          onSuccess(_)
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

  def handleTimeDelta(value: String, config: Config): IfConfig = {
    parseIntAndDo(value, 1, 4096, "--t") { (i) => config.copy(time_delta = i) }
  }

  def handleMargin(value: String, config: Config): IfConfig = {
    parseIntAndDo(value, 0, 4096, "--m") { (i) => config.copy(margin = i) }
  }

  def handleWidth(value: String, config: Config): IfConfig = {
    parseIntAndDo(value, 1, 4096, "--w") { (i) => config.copy(width = i) }
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
      val either_r = parseInt(r, 0, 255, "")
      val either_g = parseInt(g, 0, 255, "")
      val either_b = parseInt(b, 0, 255, "")
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

  def parseInt(value: String, lower: Int, upper: Int, prefix: String): IfInt = {
    val left = Left(prefix + s" must be an integer between $lower and $upper")
    def onSuccess(num: Int): IfInt = {
      (num < lower || num > upper) match {
        case true => left
        case false => Right(num)
      }
    }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }

  def parseIntAndDo(value: String, lower: Int, upper: Int, prefix: String)
  (onSuccess: Int => Config): IfConfig = {
    parseInt(value, lower, upper, prefix) match {
      case Left(s) => Left(s)
      case Right(num) => Right(onSuccess(num))
    }
  }
}
