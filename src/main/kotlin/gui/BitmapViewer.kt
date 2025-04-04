package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

/**
 * A class for displaying the bitmap stored in BasicBitmapStorage.
 * Uses Swing to create a window that shows the image.
 */
class BitmapViewer(
    private val bitmapStorage: BasicBitmapStorage,
    title: String = "Bitmap Viewer",
    private var initialCameraZ: Float = 3.0f,
    private var minCameraZ: Float = 1.0f,
    private var maxCameraZ: Float = 10.0f
) {
    private val frame: JFrame = JFrame(title)
    private var cameraPositionChangeListener: ((Float) -> Unit)? = null
    private val slider: JSlider
    private val valueLabel: JLabel

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

        // Create a slider for camera position
        slider = jSlider()


        valueLabel = JLabel("Camera Z: $initialCameraZ")

        slider.addChangeListener { e ->
            val value = (slider.value / 10.0f)
            valueLabel.text = "Camera Z: $value"
            cameraPositionChangeListener?.invoke(value)
        }

        // Create control panel
        val controlPanel = JPanel()
        controlPanel.add(JLabel("Camera Distance:"))
        controlPanel.add(slider)
        controlPanel.add(valueLabel)

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

        // Add a global key event dispatcher to handle ESC key regardless of focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            if (e.id == KeyEvent.KEY_PRESSED && e.keyCode == KeyEvent.VK_ESCAPE) {
                close()
                true // Event handled
            } else {
                false // Event not handled
            }
        }
    }

    private fun jSlider(): JSlider {
        val slider = JSlider(
            JSlider.HORIZONTAL,
            (minCameraZ * 10).toInt(),
            (maxCameraZ * 10).toInt(),
            (initialCameraZ * 10).toInt()

        )
        slider.majorTickSpacing = 10
        slider.minorTickSpacing = 1
        slider.paintTicks = true
        slider.paintLabels = true
        return slider
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
     * Sets a listener that will be called when the camera position slider is adjusted.
     * @param listener A function that takes a Float parameter representing the new camera Z position.
     */
    fun setCameraPositionChangeListener(listener: (Float) -> Unit) {
        cameraPositionChangeListener = listener
        // Call the listener with the initial value
        listener(initialCameraZ)
    }

    /**
     * Gets the current camera Z position from the slider.
     * @return The current camera Z position.
     */
    fun getCurrentCameraZ(): Float {
        return slider.value / 10.0f
    }

    /**
     * Sets the camera Z position and updates the slider.
     * This method is primarily for testing purposes.
     * @param cameraZ The new camera Z position.
     */
    fun setCameraZ(cameraZ: Float) {
        val sliderValue = (cameraZ * 10).toInt()
        slider.value = sliderValue
        // The change listener will be triggered automatically
    }
}
