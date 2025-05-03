package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import net.fredrikmeyer.State
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.ChangeEvent

/**
 * A class for displaying the bitmap stored in BasicBitmapStorage.
 * Uses Swing to create a window that shows the image.
 */
class BitmapViewer(
    private val bitmapStorage: BasicBitmapStorage,
    title: String = "Ray Tracer",
    state: State,
    minCameraZ: Float = 1.0f,
    maxCameraZ: Float = 10.0f,
    var cameraPositionChangeListener: (BitmapViewer.(Float) -> Unit)? = null
) {
    private val frame: JFrame = JFrame(title)
    private val distanceSliderValueLabel: JLabel

    private val distanceSlider = DistanceSlider(
        (minCameraZ * 10).toInt(),
        (maxCameraZ * 10).toInt(),
        (state.currentCameraZ * 10).toInt()
    )

     class DistanceSlider(
        min: Int,
        max: Int,
        initial: Int
    ) : JSlider(HORIZONTAL, min, max, initial) {
        init {
            majorTickSpacing = 10
            minorTickSpacing = 1
            paintTicks = true
            paintLabels = true
        }

        fun addChangeListener(function: (ChangeEvent) -> Unit) {
            super.addChangeListener(function)
        }
    }

    init {
        // Create a panel that displays the image
        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.drawImage(bitmapStorage.image, 0, 0, this)
            }

            override fun getPreferredSize(): Dimension {
                return Dimension(bitmapStorage.image.width, bitmapStorage.image.height)
            }
        }

        // Make the panel focusable and add key listener to it as well
        panel.isFocusable = true
        panel.requestFocusInWindow() // Request focus for the panel
        panel.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    close()
                }
            }
        })


        distanceSliderValueLabel = JLabel("Camera Z: ${state.currentCameraZ}")

        distanceSlider.addChangeListener { e ->
            val value = (distanceSlider.value / 10.0f)
            distanceSliderValueLabel.text = "Camera Z: $value"
            this@BitmapViewer.cameraPositionChangeListener?.invoke(this@BitmapViewer, value)
        }


        // Create the control panel
        val controlPanel = JPanel()
        controlPanel.add(JLabel("Camera Distance:"))
        controlPanel.add(distanceSlider)
        controlPanel.add(distanceSliderValueLabel)

        // Set up the frame with BorderLayout
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.layout = BorderLayout()
        frame.add(panel, BorderLayout.CENTER)
        frame.add(controlPanel, BorderLayout.SOUTH)
        frame.pack()
        frame.isResizable = false
        frame.isFocusable = true

        // Add key listener to close the frame when ESC is pressed
        frame.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    close()
                }
            }
        })

        // Add key listener to the content pane as well
        frame.contentPane.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    close()
                }
            }
        })
        frame.contentPane.isFocusable = true

        // Add a global key event dispatcher to handle the ESC key regardless of focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            if (e.id == KeyEvent.KEY_PRESSED && e.keyCode == KeyEvent.VK_ESCAPE) {
                close()
                true // Event handled
            } else {
                false // Event not handled
            }
        }
    }

    /**
     * Shows the bitmap viewer window.
     * @param center If true, centers the window on the screen.
     */
    fun show(center: Boolean = true) {
        SwingUtilities.invokeLater {
            if (center) {
                frame.setLocationRelativeTo(null)
            }
            frame.isVisible = true
            frame.requestFocus()
        }
    }

    /**
     * Refreshes the display to show any changes made to the bitmap.
     */
    fun refresh() {
        SwingUtilities.invokeLater {
            frame.repaint()
            frame.requestFocus()
        }
    }

    /**
     * Closes the viewer window.
     */
    fun close() {
        SwingUtilities.invokeLater {
            frame.dispose()
        }
    }

    /**
     * Checks if the viewer window is visible.
     * @return true if the window is visible, false otherwise.
     */
    fun isVisible(): Boolean {
        return frame.isVisible
    }

    /**
     * Gets the current camera Z position from the slider.
     * @return The current camera Z position.
     */
    fun getCurrentCameraZ(): Float {
        return distanceSlider.value / 10.0f
    }

    /**
     * Sets the camera Z position and updates the slider.
     * This method is primarily for testing purposes.
     * @param cameraZ The new camera Z position.
     */
    fun setCameraZ(cameraZ: Float) {
        val sliderValue = (cameraZ * 10).toInt()
        distanceSlider.value = sliderValue
        // The change listener will be triggered automatically
    }
}
