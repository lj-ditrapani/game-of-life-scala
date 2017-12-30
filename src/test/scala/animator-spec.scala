package info.ditrapani.gameoflife

import java.util.concurrent.atomic.AtomicReference
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, times, verify}
import org.scalatest.EitherValues
import org.scalatest.mockito.MockitoSugar

class AnimatorSpec extends Spec with MockitoSugar with EitherValues {
  describe("construction") {
    describe("when the gridRef is empty") {
      it("raises an exception") {
        val gridRef = new AtomicReference[Option[Grid]](None)
        val sceneDrawer = mock[SceneDrawer]
        an[AssertionError] should be thrownBy {
          AnimatorFactoryImpl(gridRef, sceneDrawer)
        }
        verify(sceneDrawer, never()).drawScene(any[Grid])
        gridRef.get() shouldBe None
      }
    }

    describe("when the gridRef is not empty") {
      it("does nothing") {
        val grid = Grid.build("-+-\n-+-\n-+-").right.value
        val gridRef = new AtomicReference[Option[Grid]](Some(grid))
        val sceneDrawer = mock[SceneDrawer]
        AnimatorFactoryImpl(gridRef, sceneDrawer)
        verify(sceneDrawer, never()).drawScene(grid)
        gridRef.get() shouldBe Some(grid)
      }
    }
  }

  describe("handle") {
    describe("when the gridRef is empty") {
      it("does nothing") {
        val grid = Grid.build("-+-\n-+-\n-+-").right.value
        val gridRef = new AtomicReference[Option[Grid]](Some(grid))
        val sceneDrawer = mock[SceneDrawer]
        val animator = AnimatorFactoryImpl(gridRef, sceneDrawer)
        gridRef.set(None)
        animator.handle(0)
        verify(sceneDrawer, never()).drawScene(any[Grid])
        gridRef.get() shouldBe None
      }
    }

    describe("when the gridRef is not empty") {
      it("draws the initial scene and empties the gridRef") {
        val grid = Grid.build("-+-\n-+-\n-+-").right.value
        val gridRef = new AtomicReference[Option[Grid]](Some(grid))
        val sceneDrawer = mock[SceneDrawer]
        val animator = AnimatorFactoryImpl(gridRef, sceneDrawer)
        gridRef.set(Some(grid))
        animator.handle(0)
        verify(sceneDrawer, times(1)).drawScene(grid)
        gridRef.get() shouldBe None
      }
    }
  }
}
