package info.ditrapani.game_of_life

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer

object LifeFX extends JFXApp {
  val canvas = new Canvas(800, 800)
  val gc = canvas.graphicsContext2D
  canvas.translateX = 0
  canvas.translateY = 0

  gc.setFill(Color.rgb(20, 20, 20))
  gc.fillRect(0, 0, 800, 800)

  gc.setFill(Color.rgb(200, 220, 255))

  var count = 0
  var offset = 0
  val d = 4
  val w = 16


  offset = count * (d + w)
  gc.fillRect(d + offset, d, w, w)
  count += 1
  offset = count * (d + w)
  gc.fillRect(d + offset, d, w, w)
  count += 1
  offset = count * (d + w)
  gc.fillRect(d + offset, d, w, w)
  count += 1

  stage = new JFXApp.PrimaryStage {
    title = "Hello Stage"
    scene = new Scene(800, 800) {
      content = canvas
    }
  }

  var lastTime = System.nanoTime()
  var r = 0

  AnimationTimer(t => {
    if (lastTime == 0L) {
      lastTime = t
    }
    else if (t - lastTime > 500000000) {
      lastTime = t

      gc.setFill(Color.rgb(r, 0, 100))
      // gc.clearRect(0, 0, stage.getWidth(), stage.getHeight())
      gc.fillRect(0, 0, stage.getWidth(), stage.getHeight())
      r += 10
      r = r % 256
      // computer next cell table
      // render cell table
    }
  }).start()
}

case class Adder(val a: Int) {
  def add(b: Int): Adder = Adder(a + b)
  def +(b: Adder): Adder = Adder(a + b.a)
}
