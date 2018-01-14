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
    val inputStream = getClass.getResourceAsStream("/help.txt")
    scala.io.Source.fromInputStream(inputStream).mkString
  }
}

class Terminator(printer: Printer, killer: Killer, helpTextLoader: HelpTextLoader) {
  def help(): Unit = {
    val helpText = helpTextLoader.load()
    printer.print(helpText)
    for ((name, index) <- Config.boards.zipWithIndex) {
      printer.print(s"    ${index + 1}  $name")
    }
    printer.print("\n")
    killer.kill()
  }

  def error(message: String): Unit = {
    printer.print(s"\n[ERROR] $message\n")
    help()
  }
}
