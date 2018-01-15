package info.ditrapani.gameoflife

import java.util.concurrent.atomic.AtomicReference
import monix.execution.Scheduler.Implicits.global
import org.scalatest.EitherValues
import org.scalatest.OptionValues

class InfinitySpec extends Spec {
  describe("decrement") {
    it("returns this") {
      Infinity.decrement() shouldBe Infinity
    }
  }

  describe("isDone") {
    it("returns false") {
      Infinity.isDone() shouldBe false
    }
  }
}

class CountSpec extends Spec {
  describe("decrement") {
    it("returns Count(i - 1)") {
      Count(2).decrement() shouldBe Count(1)
    }
  }

  describe("isDone") {
    describe("when positive") {
      it("returns false") {
        Count(1).isDone() shouldBe false
      }
    }

    describe("when 0") {
      it("returns true") {
        Count(0).isDone() shouldBe true
      }
    }
  }
}

class StepperImplSpec extends AsyncSpec with EitherValues with OptionValues {
  describe("run") {
    describe("when the gridRef is not empty") {
      describe("and the iterations is 1") {
        it("does not compute the next grid, but leaves the grid as is") {
          val grid = Grid.build("---\n-+-\n---").right.value
          val gridRef = new AtomicReference[Option[Grid]](Some(grid))
          val stepper = new Stepper(gridRef, 0)
          stepper
            .run(grid, Count(1))
            .runAsync
            .map(unused => {
              gridRef.get().value.toString shouldBe "---\n-+-\n---"
            })
        }
      }
    }

    describe("when the gridRef is not empty empty") {
      describe("and the iterations is 2") {
        it("computes the next grid and then does nothing") {
          val grid = Grid.build("----\n++++\n----").right.value
          val gridRef = new AtomicReference[Option[Grid]](None)
          val stepper = new Stepper(gridRef, 0)
          stepper
            .run(grid, Count(2))
            .runAsync
            .map(unused => {
              gridRef.get().value.toString shouldBe "++++\n++++\n++++"
            })
        }
      }
    }
  }
}
