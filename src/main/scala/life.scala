package info.ditrapani.game_of_life

object Life {
  def main(args: Array[String]): Unit = {
    println("Hi!")
  }
}

case class Adder(val a: Int) {
  def add(b: Int): Adder = Adder(a + b)
  def +(b: Adder): Adder = Adder(a + b.a)
}
