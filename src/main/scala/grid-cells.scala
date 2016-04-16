package info.ditrapani.gameoflife

case class Grid(val cells: Vector[Vector[Cell]]) {
  val height = cells.size
  val width = cells.head.size

  override def toString: String = cells.map {
    rows => rows.map(_.toChar).mkString
  }.mkString("\n")

  def countAliveNeighbors(row: Int, column: Int): Int = {
    def offset(x: Int, dx: Int, size: Int): Int = (x + dx) match {
      case -1 => size - 1
      case wrap if wrap == size => 0
      case x2 => x2
    }
    def isAlive(neighbor_delta: (Int, Int)): Boolean = {
      val (row_delta, column_delta) = neighbor_delta
      val new_row = offset(row, row_delta, height)
      val new_column = offset(column, column_delta, width)
      cells(new_row)(new_column).alive
    }
    Grid.neighbor_deltas.map(isAlive).count(x => x)
  }

  def nextCell(row: Int, column: Int): Cell = {
    cells(row)(column).next(countAliveNeighbors(row, column))
  }

  def next: Grid = {
    val seq_of_vecs = for (row <- 0 until height) yield {
      val cell_seq = for (column <- 0 until width) yield {
        nextCell(row, column)
      }
      cell_seq.to[Vector]
    }
    val next_cells = seq_of_vecs.to[Vector]
    Grid(next_cells)
  }
}

object Grid {
  val neighbor_deltas = List(
    (-1, -1), (-1, 0), (-1, 1),
    ( 0, -1),          ( 0, 1),
    ( 1, -1), ( 1, 0), ( 1, 1)
  )

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
