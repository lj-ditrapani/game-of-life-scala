package info.ditrapani.game_of_life

import javax.swing.{JFrame, JPanel}
import java.awt.{Graphics, Graphics2D, Color}

object Life extends Runnable {
  def main(args: Array[String]): Unit = {
    println("Hi!")
    javax.swing.SwingUtilities.invokeLater(Life)
  }

  override def run(): Unit = {
    val frame = new JFrame("Game of Life")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val panel = new Panel()
    val _ = frame.getContentPane().add(panel)

    frame.pack()
    frame.setVisible(true)
  }
}

class Panel extends JPanel() {
  override def paintComponent(basic_g: Graphics): Unit = {
    super.paintComponent(basic_g)
    val g = basic_g.create.asInstanceOf[Graphics2D]
    g.setColor(new Color(100, 120, 255))
    val margin = 10
    val width = 30
    var offset_x = 0 * (margin + width) + margin
    g.fillRect(offset_x, margin, offset_x + width, width)
    g.setColor(new Color(50, 60, 155))
    offset_x = 1 * (margin + width) + margin
    g.fillRect(offset_x, offset_x, offset_x + width, offset_x + width)
  }
}

case class Adder(val a: Int) {
  def add(b: Int): Adder = Adder(a + b)
  def +(b: Adder): Adder = Adder(a + b.a)
}
