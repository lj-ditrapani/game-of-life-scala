package info.ditrapani.gameoflife

case class Grid(val cells: Vector[Vector[Cell]]) {
  override def toString: String = cells.map {
    rows => rows.map(_.toChar).mkString
  }.mkString("\n")

  def aliveNeighbors(row: Int, col: Int): Int = 3
}

object Grid {
  def build(str: String): Either[String, Grid] = {
    val lines = str.split("\n").to[Vector]
    if ((lines.size < 3) || (lines.head.size < 3)) {
      Left("Board must be at least 3 x 3")
    } else if (!lineLengthsMatch(lines)) {
      Left("Board line lengths don't match")
    } else if (!onlyPlusesAndDashes(lines)) {
      Left("Board must contain only + and - characters")
    } else {
      val cells = lines.map(_.map(Cell.get(_)).to[Vector])
      Right(Grid(cells))
    }
  }

  def lineLengthsMatch(lines: Vector[String]): Boolean = {
    val init_size = lines.head.size
    lines.map(_.size).forall(_ == init_size)
  }

  def onlyPlusesAndDashes(lines: Vector[String]): Boolean = {
    lines.forall(_.forall(char => char == '-' || char == '+'))
  }
}

case class Cell(alive: Boolean) {
  def toChar: Char = if (alive) '+' else '-'

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
