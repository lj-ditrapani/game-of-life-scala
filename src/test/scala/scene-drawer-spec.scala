package info.ditrapani.gameoflife

import config.{BoardSource, Config}
import org.scalatest.EitherValues
import scalafx.scene.paint.Color

final case class Draw(color: Color, x: Int, y: Int, width: Int)

@SuppressWarnings(Array("org.wartremover.warts.Var"))
class BoxDrawerFake extends BoxDrawer {
  private var buffer = List[Draw]()

  def draw(color: Color, x: Int, y: Int, width: Int): Unit =
    buffer = Draw(color, x, y, width) :: buffer

  def getDraws(): List[Draw] = buffer.reverse
}

class SceneDrawerSpec extends Spec with EitherValues {
  describe("drawScene") {
    it("draws the cells as a series of boxes") {
      val config: Config = Config.defaultConfig(BoardSource.BuiltIn(1))
      val boxDrawer = new BoxDrawerFake()
      val sceneDrawer = SceneDrawerFactoryImpl(config, boxDrawer)
      val grid = Grid.build("--+\n+--\n++-").right.value
      sceneDrawer.drawScene(grid)
      val alive = Color.rgb(200, 220, 255)
      val dead = Color.rgb(90, 100, 130)
      boxDrawer.getDraws() shouldBe List(
        // format: off
        Draw(dead,   4, 4, 16),
        Draw(dead,  24, 4, 16),
        Draw(alive, 44, 4, 16),

        Draw(alive,  4, 24, 16),
        Draw(dead,  24, 24, 16),
        Draw(dead,  44, 24, 16),

        Draw(alive,  4, 44, 16),
        Draw(alive, 24, 44, 16),
        Draw(dead,  44, 44, 16)
        // format: on
      )
    }
  }
}
