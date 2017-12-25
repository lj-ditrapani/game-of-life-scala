package info.ditrapani.gameoflife

import config.Config
import scalafx.scene.canvas.GraphicsContext
import monix.eval.Task
import scalafx.scene.paint.Color

class SceneDrawer(config: Config, gc: GraphicsContext) {
  private def tupleRgb = Function.tupled[Int, Int, Int, Color](Color.rgb)
  private val alive_color = tupleRgb(config.alive_color)
  private val dead_color = tupleRgb(config.dead_color)
  private val margin = config.margin
  private val width = config.width

  def drawScene(grid: Grid): Task[Unit] =
    Task.eval(effects(grid))

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private def effects(grid: Grid): Unit = {
    var x = width * -1
    var y = width * -1
    for (row <- grid.cells) {
      x = width * -1
      y += (width + margin)
      for (cell <- row) {
        x += (width + margin)
        val color = if (cell.alive) alive_color else dead_color
        gc.setFill(color)
        gc.fillRect(x.toDouble, y.toDouble, width.toDouble, width.toDouble)
      }
    }
  }
}
