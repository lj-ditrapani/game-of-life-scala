package info.ditrapani.gameoflife

final case class Grid(val cells: Vector[Vector[Cell]]) {
  val height: Int = cells.size
  val width: Int = cells.headOption.map(_.size).getOrElse(0)

  override def toString: String =
    cells
      .map { rows =>
        rows.map(_.toChar).mkString
      }
      .mkString("\n")

  def countAliveNeighbors(row: Int, column: Int): Int = {
    def offset(x: Int, dx: Int, size: Int): Int = (x + dx) match {
      case -1 => size - 1
      case `size` => 0
      case x2 => x2
    }
    def isAlive(neighborDelta: (Int, Int)): Boolean = {
      val (rowDelta, columnDelta) = neighborDelta
      val newRow = offset(row, rowDelta, height)
      val newColumn = offset(column, columnDelta, width)
      cells(newRow)(newColumn).alive
    }
    Grid.neighborDelta.map(isAlive).count(x => x)
  }

  def nextCell(row: Int, column: Int): Cell = {
    cells(row)(column).next(countAliveNeighbors(row, column))
  }

  def next: Grid = {
    val seqOfVecs = for (row <- 0 until height) yield {
      val cellSeq = for (column <- 0 until width) yield {
        nextCell(row, column)
      }
      cellSeq.to[Vector]
    }
    val nextCells = seqOfVecs.to[Vector]
    Grid(nextCells)
  }
}

object Grid {

  // format: off
  private val neighborDelta = List(
    (-1, -1), (-1, 0), (-1, 1),
    ( 0, -1),          ( 0, 1),
    ( 1, -1), ( 1, 0), ( 1, 1)
  )
  // format: on

  def build(str: String): Either[String, Grid] = {
    val lines = str.split("\n").to[Vector]
    if ((lines.size < 3) || (width(lines) < 3)) {
      Left[String, Grid]("Board must be at least 3 x 3")
    } else if (!lineLengthsMatch(lines)) {
      Left[String, Grid]("Board line lengths don't match")
    } else if (!onlyPlusesAndDashes(lines)) {
      Left[String, Grid]("Board must contain only + and - characters")
    } else {
      val cells = lines.map(_.map(Cell.fromChar(_)).to[Vector])
      Right[String, Grid](Grid(cells))
    }
  }

  def width(lines: Vector[String]): Int =
    lines.headOption.map(_.size).getOrElse(0)

  def lineLengthsMatch(lines: Vector[String]): Boolean = {
    val initSize = width(lines)
    lines.map(_.size).forall(_ == initSize)
  }

  def onlyPlusesAndDashes(lines: Vector[String]): Boolean = {
    lines.forall(_.forall(char => char == '-' || char == '+'))
  }
}
