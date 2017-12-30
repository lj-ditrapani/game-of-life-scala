package info.ditrapani.gameoflife

import config.Config
import java.util.concurrent.atomic.AtomicReference
import terminator.Terminator
import monix.execution.Scheduler.Implicits.global

class Life(
    boardLoader: BoardLoader,
    javaFxApp: JavaFxApp,
    sceneDrawerFactory: SceneDrawerFactory,
    animatorFactory: AnimatorFactory,
    stepperFactory: StepperFactory
) {

  def main(params: Params, terminator: Terminator): Unit =
    Config
      .parse(params.unnamed, params.named)
      .flatMap(runGame)
      .left
      .foreach(terminator.printErrorHelpAndExit)

  def runGame(config: Config): Either[String, Unit] =
    boardLoader.getBoardStr(config.board_source).flatMap { board_str =>
      Grid.build(board_str).map(startGfx(_, config))
    }

  def startGfx(grid: Grid, config: Config): Unit = {
    val boxDrawer = javaFxApp.createSceneAndBoxDrawer(grid, config)
    val sceneDrawer = sceneDrawerFactory(config, boxDrawer)
    val gridRef = new AtomicReference[Option[Grid]](Some(grid))
    stepperFactory(gridRef, config.time_delta).run(grid, Infinity).runAsync
    animatorFactory(gridRef, sceneDrawer).run()
    (): Unit
  }
}
