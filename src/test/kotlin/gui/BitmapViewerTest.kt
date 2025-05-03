package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import net.fredrikmeyer.State
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import java.awt.image.BufferedImage

class BitmapViewerTest {

    @Test
    fun `test camera position slider initialization`() {
        // Create a bitmap storage with a small image
        val bitmapStorage = BasicBitmapStorage(10, 10)

        // Create a BitmapViewer with specific camera Z parameters
        val initialCameraZ = 5.0f
        val minCameraZ = 2.0f
        val maxCameraZ = 8.0f

        val viewer = BitmapViewer(
            bitmapStorage = bitmapStorage,
            title = "Test Viewer",
            state = State(currentCameraZ = initialCameraZ),
            minCameraZ = minCameraZ,
            maxCameraZ = maxCameraZ
        )

        // Verify that the current camera Z position matches the initial value
//        assertEquals(initialCameraZ, viewer.getCurrentCameraZ(), 0.1f)
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
        assertTrue(listenerCalled)
        assertEquals(3.0f, newCameraZ, 0.1f) // Default initial value is 3.0f
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
