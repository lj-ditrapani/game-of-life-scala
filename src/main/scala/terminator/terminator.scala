package info.ditrapani.gameoflife.terminator

import info.ditrapani.gameoflife.config.Config

trait Printer {
  def print(s: String): Unit
}

trait Killer {
  def kill(): Unit
}

trait HelpTextLoader {
  def load(): String
}

object PrinterImpl extends Printer {
  def print(s: String): Unit = println(s) // scalastyle:ignore
}

object KillerImpl extends Killer {
  def kill(): Unit = System.exit(0)
}

object HelpTextLoaderImpl extends HelpTextLoader {

  def load(): String = {
    val input_stream = getClass.getResourceAsStream("/help.txt")
    scala.io.Source.fromInputStream(input_stream).mkString
  }
}

class Terminator(printer: Printer, killer: Killer, helpTextLoader: HelpTextLoader) {

  def printErrorHelpAndExit(message: String): Unit = {
    if (message != "Printing help text...") {
      printer.print(s"\n[ERROR] $message\n")
    }
    val help_text = helpTextLoader.load()
    printer.print(help_text)
    for ((name, index) <- Config.boards.zipWithIndex) {
      printer.print(s"    ${index + 1}  $name")
    }
    printer.print("\n")
    killer.kill()
  }
}
