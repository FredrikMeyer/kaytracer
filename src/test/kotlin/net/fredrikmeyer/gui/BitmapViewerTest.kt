package net.fredrikmeyer.gui


import net.fredrikmeyer.BasicBitmapStorage
import net.fredrikmeyer.State
import org.assertj.core.api.Assertions.assertThat
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue


class BitmapViewerTest {
    private lateinit var window: FrameFixture

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUpOnce() {
            FailOnThreadViolationRepaintManager.install()
        }
    }

    class BitMapApplication {
        var frame: javax.swing.JFrame? = null

        fun createAndShow(): javax.swing.JFrame {
            val viewer = BitmapViewer(
                bitmapStorage = BasicBitmapStorage(10, 10),
                title = "Test Viewer",
                state = State(),
            )
            viewer.show()
            // Get the JFrame from the viewer
            val frameField = BitmapViewer::class.java.getDeclaredField("frame")
            frameField.isAccessible = true
            frame = frameField.get(viewer) as javax.swing.JFrame
            return frame!!
        }
    }


    @BeforeEach
    fun setUp() {
        // Create the application and get the frame on the EDT
        val application = BitMapApplication()
        val frame = GuiActionRunner.execute<javax.swing.JFrame> { 
            application.createAndShow() 
        }

        // Create the FrameFixture with the actual JFrame
        window = FrameFixture(frame)
        window.show() // shows the frame to test
    }

    @AfterEach
    fun tearDown() {
        window.cleanUp()
    }

    @Test
    fun `test camera position slider initialization`() {
        // Find the slider in the frame by name
        val slider = window.slider("cameraZSlider")

        // Verify the slider exists
        slider.requireVisible()

        // Click on the slider
        slider.click()

        // Move the slider to a specific value (must be within the valid range 10-100)
        slider.slideTo(50)

        // Verify the slider value using the correct method
        assertEquals(50, slider.target().value)
    }

    @Disabled("Inntil finner måte å mocke opp GUI")
    @Test
    fun `test camera position change listener`() {
        // Create a bitmap storage with a small image
        val bitmapStorage = BasicBitmapStorage(10, 10)

        // Create a variable to track if the listener was called
        var listenerCalled = false
        var newCameraZ = 0.0f

        // Create a BitmapViewer with default camera Z parameters
        val viewer = BitmapViewer(
            bitmapStorage, "Test Viewer", State(),
            cameraPositionChangeListener = { cameraZ ->
                listenerCalled = true
                newCameraZ = cameraZ
            }
        )

        viewer.refresh()

        // Verify that the listener was called with the initial value
        assertThat(listenerCalled).isTrue()
        assertThat(newCameraZ).isEqualTo(3.0f)
    }

    @Disabled("Inntil finner måte å mocke opp GUI")
    @Test
    fun `test camera position update`() {
        // Create a bitmap storage with a small image
        val bitmapStorage = BasicBitmapStorage(10, 10)

        // Create a variable to track the camera position
        var currentCameraZ = 0.0f

        // Create a BitmapViewer with default camera Z parameters
        val viewer = BitmapViewer(
            bitmapStorage,
            "Test Viewer",
            State(),
            cameraPositionChangeListener = { cameraZ ->
                currentCameraZ = cameraZ
            })

        // Verify that the initial camera position is set correctly
        assertEquals(3.0f, currentCameraZ, 0.1f)

        // Simulate changing the slider value using the setCameraZ method
        val newCameraZ = 7.0f
//        viewer.setCameraZ(newCameraZ)

        // Verify that the camera position was updated
        assertEquals(newCameraZ, currentCameraZ, 0.1f)

        // Also verify that getCurrentCameraZ returns the correct value
//        assertEquals(newCameraZ, viewer.getCurrentCameraZ(), 0.1f)
    }
}
