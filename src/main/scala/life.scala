package info.ditrapani.gameoflife

import config.Config
import terminator.Terminator

class Life(
    javaFxApp: JavaFxApp,
    sceneDrawerFactory: SceneDrawerFactory,
    animatorFactory: AnimatorFactory
) {

  def main(params: Params, terminator: Terminator): Unit =
    Config
      .parse(params.unnamed, params.named)
      .map(runGame)
      .left
      .foreach(terminator.printErrorHelpAndExit)

  def runGame(config: Config): Either[String, Unit] =
    BoardLoader.getBoardStr(config.board_source).flatMap { board_str =>
      Grid.build(board_str).map(startGfx(_, config))
    }

  def startGfx(grid: Grid, config: Config): Unit = {
    val boxDrawer = javaFxApp.createSceneAndBoxDrawer(grid, config)
    val sceneDrawer = sceneDrawerFactory(config, boxDrawer)
    animatorFactory(grid, config, sceneDrawer).run()
  }
}
