package info.ditrapani.game_of_life

case class Grid(val cells: Vector[Vector[Cell]]) {
  override def toString: String = cells.map {
    rows => rows.map(_.to_char).mkString
  }.mkString("\n")

  def alive_neighbors(row: Int, col: Int): Int = 3
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
      def is_alive(char: Char): Boolean = char == '+'
      val cells = lines.map(_.map(Cell.get(_)).to[Vector])
      Right(Grid(cells))
    }
  }

  def line_lengths_match(lines: Vector[String]): Boolean = {
    val init_size = lines.head.size
    lines.map(_.size).forall(_ == init_size)
  }

  def only_pluses_and_dashes(lines: Vector[String]): Boolean = {
    lines.forall(_.forall(char => char == '-' || char == '+'))
  }
}

case class Cell(alive: Boolean) {
  def to_char: Char = if (alive) '+' else '-'

  def next(neighbor_count: Int): Cell = neighbor_count match {
    case x if x < 2 => Cell.dead
    case 2 => Cell.get(alive)
    case 3 => Cell.living
    case _ => Cell.dead
  }
}

object Cell {
  val living = Cell(true)

  val dead = Cell(false)

  def get(alive: Boolean): Cell = {
    if (alive) Cell.living else Cell.dead
  }

  def get(char: Char): Cell = {
    get(char == '+')
  }
}
