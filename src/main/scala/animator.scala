package info.ditrapani.gameoflife

import java.util.concurrent.atomic.AtomicReference
import javafx.animation.AnimationTimer

trait AnimatorFactory {

  def apply(gridRef: AtomicReference[Option[Grid]], sceneDrawer: SceneDrawer): Animator
}

object AnimatorFactoryImpl extends AnimatorFactory {

  def apply(gridRef: AtomicReference[Option[Grid]], sceneDrawer: SceneDrawer): Animator =
    new AnimatorImpl(gridRef, sceneDrawer)
}

trait Animator {
  def run(): Unit
}

class AnimatorImpl(gridRef: AtomicReference[Option[Grid]], sceneDrawer: SceneDrawer)
    extends AnimationTimer
    with Animator {
  gridRef.get() match {
    case Some(grid) =>
      sceneDrawer.drawScene(grid)
    case None =>
      assert(!gridRef.get().isEmpty)
  }

  def handle(curr_time: Long): Unit = {
    val maybeGrid = gridRef.getAndSet(None)
    maybeGrid match {
      case None => (): Unit
      case Some(grid) => sceneDrawer.drawScene(grid)
    }
  }

  def run(): Unit = start()
}
