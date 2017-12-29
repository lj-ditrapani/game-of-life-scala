package info.ditrapani.gameoflife

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit
import monix.eval.Task
import scala.concurrent.duration.FiniteDuration

sealed abstract class Iterations {
  def decrement(): Iterations
  def isDone(): Boolean
}

object Infinity extends Iterations {
  def decrement(): Iterations = this
  def isDone(): Boolean = false
}

final case class Count(i: Int) extends Iterations {
  def decrement(): Iterations = Count(i - 1)
  def isDone(): Boolean = i <= 0
}

trait StepperFactory {
  def apply(gridRef: AtomicReference[Option[Grid]], time_delta: Int): Stepper
}

object StepperFactoryImpl extends StepperFactory {

  def apply(gridRef: AtomicReference[Option[Grid]], time_delta: Int): Stepper =
    new Stepper(gridRef, time_delta)
}

class Stepper(gridRef: AtomicReference[Option[Grid]], time_delta: Int) {

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def run(grid: Grid, iterations: Iterations): Task[Unit] =
    if (iterations.isDone()) {
      Task.now((): Unit)
    } else {
      val next_grid = gridRef.get() match {
        case Some(_) =>
          grid
        case None =>
          val grid2 = grid.next
          gridRef.set(Some(grid2))
          grid2
      }
      Task
        .defer(run(next_grid, iterations.decrement()))
        .delayExecution(new FiniteDuration(time_delta.toLong, TimeUnit.MILLISECONDS))
    }
}
