package info.ditrapani.gameoflife

final case class Cell(alive: Boolean) {
  def toChar: Char = if (alive) '+' else '-'

  def next(neighbor_count: Int): Cell = neighbor_count match {
    case x if x < 2 => Cell.dead
    case 2 => Cell.fromBool(alive)
    case 3 => Cell.living
    case _ => Cell.dead
  }
}

object Cell {
  val living: Cell = Cell(true)

  val dead: Cell = Cell(false)

  def fromBool(alive: Boolean): Cell = {
    if (alive) Cell.living else Cell.dead
  }

  def fromChar(char: Char): Cell = {
    fromBool(char == '+')
  }
}
