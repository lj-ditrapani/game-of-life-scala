package info.ditrapani.gameoflife.config

import scala.util.{Try, Success, Failure}
import scala.util.matching.Regex.Match

object BoardSource {
  sealed abstract class Source
  final case class BuiltIn(index: Int) extends Source
  final case class File(path: String) extends Source
}

final case class Config(
    board_source: BoardSource.Source,
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

  def defaultConfig(boardSource: BoardSource.Source): Config =
    Config(
      boardSource,
      500,
      4,
      16,
      (200, 220, 255),
      (90, 100, 130),
      (150, 170, 200)
    )

  def parse(help_params: List[String], params: Map[String, String]): IfConfig =
    if (help_params.exists(_ == "--help")) {
      Left("Printing help text...")
    } else if (!help_params.isEmpty) {
      Left(s"Unknown command line parameter in ${help_params}")
    } else {
      getSource(params).flatMap {
        case (source, new_params) => {
          val zero: IfConfig = Right(Config.defaultConfig(source))
          new_params.foldLeft(zero) { (if_config, kv) =>
            if_config.flatMap { addParams(kv, _) }
          }
        }
      }
    }

  private def getSource(
      params: Map[String, String]
  ): Either[String, (BoardSource.Source, Map[String, String])] = {
    if (params.contains("b") && params.contains("f")) {
      Left("Cannot define both --b and --f as board source; pick one")
    } else if (params.contains("b")) {
      parseBuiltIn(params("b")).map(i => (BoardSource.BuiltIn(i), params - "b"))
    } else if (params.contains("f")) {
      Right((BoardSource.File(params("f")), params - "f"))
    } else {
      Left("Must define either --b or --f as board source")
    }
  }

  private def addParams(kv: (String, String), config: Config): IfConfig = {
    val (flag, value) = kv
    flag match {
      case "t" => handleTimeDelta(value, config)
      case "m" => handleMargin(value, config)
      case "w" => handleWidth(value, config)
      case "alive-color" => handleAliveColor(value, config)
      case "dead-color" => handleDeadColor(value, config)
      case "bg-color" => handleBgColor(value, config)
      case _ => Left(s"Unknown command line parameter '--${flag}'")
    }
  }

  private def parseBuiltIn(value: String): Either[String, Int] =
    parseInt(value, 1, board_count, "Invalid value for --b,").map(_ - 1)

  private def handleTimeDelta(value: String, config: Config): IfConfig =
    parseInt(value, 1, 4096, "--t").map { (i) =>
      config.copy(time_delta = i)
    }

  private def handleMargin(value: String, config: Config): IfConfig =
    parseInt(value, 0, 4096, "--m").map { (i) =>
      config.copy(margin = i)
    }

  private def handleWidth(value: String, config: Config): IfConfig =
    parseInt(value, 1, 4096, "--w").map { (i) =>
      config.copy(width = i)
    }

  private def handleAliveColor(value: String, config: Config): IfConfig =
    handleColor(value, "alive").map { color =>
      config.copy(alive_color = color)
    }

  private def handleDeadColor(value: String, config: Config): IfConfig =
    handleColor(value, "dead").map { color =>
      config.copy(dead_color = color)
    }

  private def handleBgColor(value: String, config: Config): IfConfig =
    handleColor(value, "bg").map { color =>
      config.copy(bg_color = color)
    }

  def handleColor(value: String, color_type: String): Either[String, (Int, Int, Int)] = {
    val pattern = "^(\\d{1,3}),(\\d{1,3}),(\\d{1,3})$".r
    val errorMessage = s"--$color_type-color must be Int,Int,Int between 0-255"

    def processMatch(m: Match): Either[String, (Int, Int, Int)] =
      for {
        ri <- parseInt(m.group(1), 0, 255, "")
        gi <- parseInt(m.group(2), 0, 255, "")
        bi <- parseInt(m.group(3), 0, 255, "")
      } yield (ri, gi, bi)

    pattern
      .findFirstMatchIn(value)
      .toRight[String]("")
      .flatMap(processMatch)
      .left
      .map(_ => errorMessage)
  }

  def parseInt(value: String, lower: Int, upper: Int, prefix: String): IfInt = {
    val left = Left(prefix + s" must be an integer between $lower and $upper")
    def onSuccess(num: Int): IfInt =
      (num < lower || num > upper) match {
        case true => left
        case false => Right(num)
      }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }
}
