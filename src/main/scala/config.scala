package info.ditrapani.gameoflife

import scala.util.{Try, Success, Failure}

object BoardSource extends Enumeration {
  type Source = Value
  val BuiltIn, File, UnSet = Value
}

case class Config(
  board_source: BoardSource.Source,
  board_str: String,
  time_delta: Int
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
    Config(BoardSource.UnSet, "", 500)
  }

  def load(help_params: Seq[String], params: Map[String,String]): IfConfig = {
    if (help_params.exists(p => p == "--help")) {
      Left("Printing help text...")
    } else if (!help_params.isEmpty) {
      Left(s"Unknown command line parameter '${help_params.head}'")
    } else {
      val if_config: IfConfig = Right(Config.emptyConfig)
      params.foldLeft(if_config) { (if_config, kv) =>
        if_config.right.flatMap { addParams(kv, _) }
      }
    }
  }

  def addParams(kv: (String, String), config: Config): IfConfig = {
    val (flag, value) = kv
    flag match {
      case "b" => handleBuiltIn(value, config)
      case "f" => Left("Not Implemented <f>")
      case "t" => Left("Not Implemented <t>")
      case "m" => Left("Not Implemented <m>")
      case "w" => Left("Not Implemented <w>")
      case "alive-color" => Left("Not Implemented <ac>")
      case "dead-color" => Left("Not Implemented <dc>")
      case _ => Left(s"Unknown command line parameter '--${flag}'")
    }
  }

  def handleBuiltIn(value: String, config: Config): IfConfig = {
    val left = Left(
      "Invalid value for --b; must be an integer between 1 and 9"
    )

    def onSuccess(num: Int): IfConfig = {
      if (num < 1 || num > board_count)
        left
      else
        Right(config.copy(board_source = BoardSource.BuiltIn))
    }

    Try(value.toInt) match {
      case Failure(_) => left
      case Success(num) => onSuccess(num)
    }
  }
}
