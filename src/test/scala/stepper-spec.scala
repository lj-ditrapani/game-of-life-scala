package info.ditrapani.gameoflife

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
