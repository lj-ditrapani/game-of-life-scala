package info.ditrapani.game_of_life

import scalafx.application.JFXApp
import scalafx.scene.canvas.Canvas
import scalafx.scene.Scene
import scalafx.scene.paint.Color

object LifeFX extends JFXApp {
  val canvas = new Canvas(800, 800)
  val gc = canvas.graphicsContext2D
  canvas.translateX = 0
  canvas.translateY = 0

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
}

case class Adder(val a: Int) {
  def add(b: Int): Adder = Adder(a + b)
  def +(b: Adder): Adder = Adder(a + b.a)
}
