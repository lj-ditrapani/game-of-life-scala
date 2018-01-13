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
    boardLoader.getBoardStr(config.boardSource).flatMap { boardStr =>
      Grid.build(boardStr).map(startGfx(_, config))
    }

  def startGfx(grid: Grid, config: Config): Unit = {
    val boxDrawer = javaFxApp.createStageAndBoxDrawer(grid, config)
    val sceneDrawer = sceneDrawerFactory(config, boxDrawer)
    val gridRef = new AtomicReference[Option[Grid]](Some(grid))
    stepperFactory(gridRef, config.timeDelta).run(grid, Infinity).runAsync
    animatorFactory(gridRef, sceneDrawer).run()
    (): Unit
  }
}
