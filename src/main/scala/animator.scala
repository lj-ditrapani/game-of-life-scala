package info.ditrapani.gameoflife

import config.Config
import javafx.animation.AnimationTimer

trait AnimatorFactory {
  def apply(grid: Grid, config: Config, sceneDrawer: SceneDrawer): Animator
}

object AnimatorFactoryImpl extends AnimatorFactory {

  def apply(grid: Grid, config: Config, sceneDrawer: SceneDrawer): Animator =
    new AnimatorImpl(grid, config, sceneDrawer)
}

trait Animator {
  def run(): Unit
}

@SuppressWarnings(Array("org.wartremover.warts.Var"))
class AnimatorImpl(grid: Grid, config: Config, sceneDrawer: SceneDrawer)
    extends AnimationTimer
    with Animator {
  private var curr_grid = grid
  private val time_delta: Long = config.time_delta * 1000000L

  sceneDrawer.drawScene(curr_grid)
  private var last_time = System.nanoTime()

  def handle(curr_time: Long): Unit = {
    if (curr_time - last_time > time_delta) {
      last_time = curr_time
      curr_grid = curr_grid.next
      sceneDrawer.drawScene(curr_grid)
    }
  }

  def run(): Unit = start()
}
