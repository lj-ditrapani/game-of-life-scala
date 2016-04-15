package info.ditrapani.game_of_life

class Grid {
}

object Grid {
  def build(str: String): Either[String, Grid] = {
    val lines = str.split("\n").to[Vector]
    if ((lines.size < 3) || (lines.head.size < 3)) {
      Left("Board must be at least 3 x 3")
    } else if (!line_lengths_match(lines)) {
      Left("Board line lengths don't match")
    } else if (!only_pluses_and_dashes(lines)) {
      Left("Board must contain only + and - characters")
    } else {
      // contruct Grid instance
      Right(new Grid)
    }
  }
  def line_lengths_match(lines: Vector[String]): Boolean = {
    lines.map(_.size).foldLeft(true -> lines.head.size) { (pair, size) =>
      val (all_equal, init_size) = pair
      (all_equal && size == init_size) -> init_size
    }._1
  }

  def only_pluses_and_dashes(lines: Vector[String]): Boolean = {
    true
  }

  def line_lengths(str: String): Seq[Int] = {
    str.split("\n").map(_.size)
  }
}

case class Cell(alive: Boolean)
