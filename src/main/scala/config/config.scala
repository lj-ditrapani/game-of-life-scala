package info.ditrapani.gameoflife.config

import scala.util.{Try, Success, Failure}
import scala.util.matching.Regex.Match

object BoardSource {
  sealed abstract class Source
  final case class BuiltIn(index: Int) extends Source
  final case class File(path: String) extends Source
}

final case class Config(
    boardSource: BoardSource.Source,
    timeDelta: Int,
    margin: Int,
    width: Int,
    aliveColor: (Int, Int, Int),
    deadColor: (Int, Int, Int),
    bgColor: (Int, Int, Int)
)

object Config {
  type IfConfig = Either[String, Config]
  type IfInt = Either[String, Int]

  val boards: Vector[String] = Vector(
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
  val boardCount: Int = Config.boards.size

  def defaultConfig(boardSource: BoardSource.Source): Config =
    Config(
      boardSource,
      timeDelta = 500,
      margin = 4,
      width = 16,
      aliveColor = (200, 220, 255),
      deadColor = (90, 100, 130),
      bgColor = (150, 170, 200)
    )

  def parse(helpParams: List[String], params: Map[String, String]): IfConfig =
    if (helpParams.exists(_ == "--help")) {
      Left[String, Config]("Printing help text...")
    } else if (!helpParams.isEmpty) {
      Left[String, Config](s"Unknown command line parameter in ${helpParams}")
    } else {
      getSource(params).flatMap {
        case (source, newParams) => {
          val zero: IfConfig = Right[String, Config](Config.defaultConfig(source))
          newParams.foldLeft(zero) { (ifConfig, kv) =>
            ifConfig.flatMap { addParams(kv, _) }
          }
        }
      }
    }

  type SourceWithParams = (BoardSource.Source, Map[String, String])

  private def getSource(
      params: Map[String, String]
  ): Either[String, (BoardSource.Source, Map[String, String])] = {
    if (params.contains("b") && params.contains("f")) {
      Left[String, SourceWithParams]("Cannot define both --b and --f as board source; pick one")
    } else if (params.contains("b")) {
      parseBuiltIn(params("b")).map(i => (BoardSource.BuiltIn(i), params - "b"))
    } else if (params.contains("f")) {
      Right[String, SourceWithParams]((BoardSource.File(params("f")), params - "f"))
    } else {
      Left[String, SourceWithParams]("Must define either --b or --f as board source")
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
      case _ => Left[String, Config](s"Unknown command line parameter '--${flag}'")
    }
  }

  private def parseBuiltIn(value: String): Either[String, Int] =
    parseInt(value, 1, boardCount, "Invalid value for --b,").map(_ - 1)

  private def handleTimeDelta(value: String, config: Config): IfConfig =
    parseInt(value, 1, 4096, "--t").map { (i) =>
      config.copy(timeDelta = i)
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
      config.copy(aliveColor = color)
    }

  private def handleDeadColor(value: String, config: Config): IfConfig =
    handleColor(value, "dead").map { color =>
      config.copy(deadColor = color)
    }

  private def handleBgColor(value: String, config: Config): IfConfig =
    handleColor(value, "bg").map { color =>
      config.copy(bgColor = color)
    }

  def handleColor(value: String, colorType: String): Either[String, (Int, Int, Int)] = {
    val pattern = "^(\\d{1,3}),(\\d{1,3}),(\\d{1,3})$".r
    val errorMessage = s"--$colorType-color must be Int,Int,Int between 0-255"

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
    val left = Left[String, Int](prefix + s" must be an integer between $lower and $upper")
    def onSuccess(num: Int): IfInt =
      (num < lower || num > upper) match {
        case true => left
        case false => Right[String, Int](num)
      }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }
}
