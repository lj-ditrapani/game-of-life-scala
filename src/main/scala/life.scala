package info.ditrapani.gameoflife

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer

object LifeFX extends JFXApp {
  Grid.build(getBoardStr).right.map(startGfx(_))

  def getBoardStr: String = {
    val input_stream = getClass.getResourceAsStream("/acorn.txt")
    scala.io.Source.fromInputStream(input_stream).mkString
  }

  def startGfx(grid: Grid): Unit = {
    val margin = 4
    val width = 16
    val canvas_height = (width + margin) * grid.height + margin
    val canvas_width = (width + margin) * grid.width + margin
    val canvas = new Canvas(canvas_width, canvas_height)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    gc.setFill(Color.rgb(20, 20, 20))
    gc.fillRect(0, 0, canvas_width, canvas_height)

    val alive_color = Color.rgb(200, 220, 255)
    val dead_color = Color.rgb(100, 120, 155)

    stage = new JFXApp.PrimaryStage {
      title = "Hello Stage"
      scene = new Scene(canvas_width, canvas_height) {
        content = canvas
      }
    }

    var last_time = System.nanoTime()
    var curr_grid = grid

    def drawScene(): Unit = {
      var x = width * -1
      var y = width * -1
      for (row <- curr_grid.cells) {
        x = width * -1
        y += (width + margin)
        for (cell <- row) {
          x += (width + margin)
          val color = if (cell.alive) alive_color else dead_color
          gc.setFill(color)
          gc.fillRect(x, y, width, width)
        }
      }
    }

    drawScene()

    AnimationTimer(curr_time => {
      if (curr_time - last_time > 500000000) {
        last_time = curr_time
        curr_grid = curr_grid.next
        drawScene()
      }
    }).start()
  }
}
