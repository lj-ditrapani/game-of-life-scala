package info.ditrapani.gameoflife

import cats.data.EitherT
import config.{BoardSource, Config}
import monix.eval.Task
import scalafx.scene.canvas.GraphicsContext
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

class Main(
    getBoardStr: (BoardSource.Source => EitherT[Task, String, String]),
    printErrorHelpAndExit: (String => Task[Unit]),
    makeGfxContext: ((Grid, Config) => Task[GraphicsContext])
) {

  def main(params: Params): Task[Unit] =
    EitherT(Task.now(Config.parse(params.unnamed, params.named)))
      .flatMap(runGame)
      .leftSemiflatMap(printErrorHelpAndExit)
      .fold(identity, identity)

  def runGame(config: Config): EitherT[Task, String, Unit] =
    getBoardStr(config.board_source).flatMap { board_str =>
      EitherT(Task.now(Grid.build(board_str))).semiflatMap(startGfx(_, config))
    }

  def startGfx(grid: Grid, config: Config): Task[Unit] =
    makeGfxContext(grid, config).flatMap(gc => {
      val sceneDrawer = new SceneDrawer(config, gc)
      def step(grid: Grid): Task[Unit] = {
        val new_grid = grid.next
        sceneDrawer
          .drawScene(new_grid)
          .delayExecution(new FiniteDuration(config.time_delta.toLong, TimeUnit.MILLISECONDS))
          .flatMap(_ => step(new_grid))
      }
      sceneDrawer.drawScene(grid).flatMap(_ => step(grid))
    })
}
