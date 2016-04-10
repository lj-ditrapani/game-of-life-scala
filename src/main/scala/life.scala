package info.ditrapani.game_of_life

/*
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
*/

/*
object LifeFX extends JFXApp {
  // def main(args: Array[String]): Unit = println("Hi!")
}
*/

object Life {
  def main(args: Array[String]): Unit = {
    println("Hi!")
    println(System.getenv("JAVA_HOME"))
  }
}

case class Adder(val a: Int) {
  def add(b: Int): Adder = Adder(a + b)
  def +(b: Adder): Adder = Adder(a + b.a)
}
