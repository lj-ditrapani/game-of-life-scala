package info.ditrapani.gameoflife

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import javafx.application.Application

@SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
class ParamsSpec extends Spec with MockitoSugar {
  import scala.collection.JavaConverters._
  private val parameters = mock[Application.Parameters]

  describe("unnamed") {
    it("converts the unnamed parameters from Seq to a List") {
      when(parameters.getUnnamed()).thenReturn(Seq("a", "b").asJava)
      val p = new Params(parameters)
      p.unnamed shouldBe List("a", "b")
    }
  }

  describe("named") {
    it("converts the named parameters from a pontential mutable Map to an immutable Map") {
      when(parameters.getNamed()).thenReturn(Map("a" -> "1", "b" -> "2").asJava)
      val p = new Params(parameters)
      p.named shouldBe Map("a" -> "1", "b" -> "2")
    }
  }
}
