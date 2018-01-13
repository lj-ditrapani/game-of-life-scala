package info.ditrapani.gameoflife.terminator

import info.ditrapani.gameoflife.config.Config.boardCount
import info.ditrapani.gameoflife.Spec
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.mockito.MockitoSugar

class TerminatorSpec extends Spec with MockitoSugar {
  describe("printErrorHelpAndExit") {
    describe("when printing help text only") {
      it("shows the help text only without the error text") {
        val printer = mock[Printer]
        val killer = mock[Killer]
        val helpTextLoader = mock[HelpTextLoader]
        when(helpTextLoader.load()).thenReturn("help-text")
        val terminator = new Terminator(printer, killer, helpTextLoader)
        terminator.printErrorHelpAndExit("Printing help text...")
        verify(killer).kill()
        verify(printer).print("help-text")
        verify(printer).print("    1  acorn")
        verify(printer).print("    2  blinkers")
        verify(printer, times(boardCount + 2)).print(anyString)
      }
    }

    describe("when a normal error") {
      it("shows the error text and help text") {
        val printer = mock[Printer]
        val killer = mock[Killer]
        val helpTextLoader = mock[HelpTextLoader]
        when(helpTextLoader.load()).thenReturn("help-text")
        val terminator = new Terminator(printer, killer, helpTextLoader)
        terminator.printErrorHelpAndExit("Fire!")
        verify(killer).kill()
        verify(printer).print("\n[ERROR] Fire!\n")
        verify(printer).print("help-text")
        verify(printer).print("    1  acorn")
        verify(printer).print("    2  blinkers")
        verify(printer, times(boardCount + 3)).print(anyString)
      }
    }
  }
}

class HelpTextLoaderImplSpec extends Spec {
  describe("load()") {
    it("loads the help text") {
      HelpTextLoaderImpl.load() should startWith("Usage\n-----")
    }
  }
}
