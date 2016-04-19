package info.ditrapani.gameoflife

object BoardSource extends Enumeration {
  type Source = Value
  val BuiltIn, File, UnSet = Value
}

class Config(
  val board_source: BoardSource.Source
) {
}

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

  def load(help_params: Seq[String], params: Map[String,String]): IfConfig = {
    if (help_params.exists(p => p == "--help")) {
      Left("Printing help text...")
    } else if (!help_params.isEmpty) {
      Left(s"Unknown parameter '${help_params.head}'")
    } else {
      val config = new Config(BoardSource.UnSet)
      val either: IfConfig = Right(config)
      params.foldLeft(either) { (either, kv) => either match {
          case Left(_) => either
          case Right(c) => addParams(kv, config)
        }
      }
    }
  }

  def addParams(kv: (String, String), config: Config): IfConfig = {
    val (flag, value) = kv
    flag match {
      case "b" => Left("Not Implemented <w>")
      case "f" => Left("Not Implemented <w>")
      case "t" => Left("Not Implemented <w>")
      case "m" => Left("Not Implemented <w>")
      case "w" => Left("Not Implemented <w>")
      case _ => Left(s"Unknown command line parameter '--${flag}'")
    }
  }
}
