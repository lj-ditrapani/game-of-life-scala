package info.ditrapani.gameoflife

import config.Config
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

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
  private val aliveColor = tupleRgb(config.aliveColor)
  private val deadColor = tupleRgb(config.deadColor)
  private val margin = config.margin
  private val width = config.width

  def drawScene(grid: Grid): Unit = yLoop(grid.cells, 0, margin)

  @tailrec
  private def yLoop(rows: Vector[Vector[Cell]], rowIndex: Int, y: Int): Unit =
    if (rowIndex == rows.size) {
      (): Unit
    } else {
      val row = rows(rowIndex)
      xLoop(row, 0, y, margin)
      yLoop(rows, rowIndex + 1, y + width + margin)
    }

  @tailrec
  private def xLoop(row: Vector[Cell], colIndex: Int, y: Int, x: Int): Unit =
    if (colIndex == row.size) {
      (): Unit
    } else {
      val cell = row(colIndex)
      val color = if (cell.alive) aliveColor else deadColor
      boxDrawer.draw(color, x, y, width)
      xLoop(row, colIndex + 1, y, x + width + margin)
    }
}
