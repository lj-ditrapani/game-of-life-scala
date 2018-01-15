package info.ditrapani.gameoflife

import java.util.concurrent.atomic.AtomicReference
import javafx.animation.AnimationTimer

class Animator(gridRef: AtomicReference[Option[Grid]], sceneDrawer: SceneDrawer)
    extends AnimationTimer {
  assert(!gridRef.get().isEmpty)

  def handle(currTime: Long): Unit = {
    val maybeGrid = gridRef.getAndSet(None)
    maybeGrid match {
      case None => (): Unit
      case Some(grid) => sceneDrawer.drawScene(grid)
    }
  }

  def run(): Unit = start()
}
