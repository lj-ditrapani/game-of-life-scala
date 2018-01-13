package info.ditrapani.gameoflife

import config.Config
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

import scala.annotation.tailrec

trait BoxDrawer {
  def draw(color: Color, x: Int, y: Int, width: Int): Unit
}

class BoxDrawerImpl(gc: GraphicsContext) extends BoxDrawer {

  def draw(color: Color, x: Int, y: Int, width: Int): Unit = {
    gc.setFill(color)
    gc.fillRect(x.toDouble, y.toDouble, width.toDouble, width.toDouble)
  }
}

trait SceneDrawerFactory {
  def apply(config: Config, boxDrawer: BoxDrawer): SceneDrawer
}

object SceneDrawerFactoryImpl extends SceneDrawerFactory {

  def apply(config: Config, boxDrawer: BoxDrawer): SceneDrawer =
    new SceneDrawer(config, boxDrawer)
}

class SceneDrawer(config: Config, boxDrawer: BoxDrawer) {
  private def tupleRgb = Function.tupled[Int, Int, Int, Color](Color.rgb)
  private val alive_color = tupleRgb(config.alive_color)
  private val dead_color = tupleRgb(config.dead_color)
  private val margin = config.margin
  private val width = config.width

  def drawScene(grid: Grid): Unit = yLoop(grid.cells, 0, margin)

  @tailrec
  private def yLoop(rows: Vector[Vector[Cell]], row_index: Int, y: Int): Unit =
    if (row_index == rows.size) {
      (): Unit
    } else {
      val row = rows(row_index)
      xLoop(row, 0, y, margin)
      yLoop(rows, row_index + 1, y + width + margin)
    }

  @tailrec
  private def xLoop(row: Vector[Cell], col_index: Int, y: Int, x: Int): Unit =
    if (col_index == row.size) {
      (): Unit
    } else {
      val cell = row(col_index)
      val color = if (cell.alive) alive_color else dead_color
      boxDrawer.draw(color, x, y, width)
      xLoop(row, col_index + 1, y, x + width + margin)
    }
}
