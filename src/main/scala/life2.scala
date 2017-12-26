package info.ditrapani.gameoflife

import cats.data.EitherT
import config.{BoardSource, Config}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scalafx.application.JFXApp
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.Scene
import scalafx.scene.paint.Color

import scala.util.{Try, Success, Failure}

object Life2 extends JFXApp {
  val params = new Params(parameters)
  new Main(getBoardStrTask, printErrorHelpAndExitTask, makeGfxContextTask, SceneDrawerFactory)
    .main(params)
    .runAsync

  def printErrorHelpAndExitTask(message: String): Task[Unit] =
    Task.eval(printErrorHelpAndExit(message))

  def printErrorHelpAndExit(message: String): Unit = {
    if (message != "Printing help text...") {
      println(s"\n[ERROR] $message\n")
    }
    val input_stream = getClass.getResourceAsStream("/help.txt")
    val help_text = scala.io.Source.fromInputStream(input_stream).mkString
    println(help_text)
    for ((name, index) <- Config.boards.zipWithIndex) {
      println(s"    ${index + 1}  $name")
    }
    println("\n")
    System.exit(0)
  }

  def getBoardStrTask(board_source: BoardSource.Source): EitherT[Task, String, String] =
    EitherT(Task.eval(getBoardStr(board_source)))

  def getBoardStr(board_source: BoardSource.Source): Either[String, String] =
    board_source match {
      case BoardSource.BuiltIn(index) =>
        val name = Config.boards(index)
        val input_stream = getClass.getResourceAsStream(s"/$name.txt")
        Right(scala.io.Source.fromInputStream(input_stream).mkString)
      case BoardSource.File(path) =>
        Try(scala.io.Source.fromFile(path).mkString) match {
          case Failure(exception) => Left(exception.toString())
          case Success(board_str) => Right(board_str)
        }
    }

  def makeGfxContextTask(grid: Grid, config: Config): Task[GraphicsContext] =
    Task.eval(makeGfxContext(grid, config))

  def makeGfxContext(grid: Grid, config: Config): GraphicsContext = {
    val width = config.width
    val margin = config.margin
    val canvas_height = ((width + margin) * grid.height + margin).toDouble
    val canvas_width = ((width + margin) * grid.width + margin).toDouble
    val canvas = new Canvas(canvas_width, canvas_height)
    val gc = canvas.graphicsContext2D
    canvas.translateX = 0
    canvas.translateY = 0

    val (r, g, b) = config.bg_color
    gc.setFill(Color.rgb(r, g, b))
    gc.fillRect(0, 0, canvas_width, canvas_height)

    stage = new JFXApp.PrimaryStage {
      title = "Game of Life by L. J. Di Trapani"
      scene = new Scene(canvas_width, canvas_height) {
        content = canvas
      }
    }

    gc
  }
}
