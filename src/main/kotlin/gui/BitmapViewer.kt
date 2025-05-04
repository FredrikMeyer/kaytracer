package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import net.fredrikmeyer.State
import net.fredrikmeyer.geometry.scene
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.Hashtable
import javax.swing.*
import javax.swing.event.ChangeEvent
import kotlin.math.roundToInt

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
    val cameraPositionChangeListener: (BitmapViewer.(Float) -> Unit)? = null,
    val rotationChangeListener: (BitmapViewer.(Double) -> Unit)? = null,
) {
    private val frame: JFrame = JFrame(title)

    class DistanceSlider(
        min: Int,
        max: Int,
        initial: Int,
        scale: Double = 100.0,
    ) : JSlider(HORIZONTAL, min, max, initial) {
        init {
            majorTickSpacing = (max - min) / 5
            minorTickSpacing = (max - min) / 10
            paintTicks = true
            paintLabels = true
            labelTable = Hashtable<Int, JLabel>().apply {
                for (i in min..max step majorTickSpacing) {
                    put(i, JLabel(String.format("%.1f", i.toDouble() / scale)))
                }
            }
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


        val distanceSliderValueLabel = JLabel("Camera Z: ${state.currentCameraZ}")

        val distanceSlider = DistanceSlider(
            (minCameraZ * 10).toInt(),
            (maxCameraZ * 10).toInt(),
            (state.currentCameraZ * 10).toInt(),
            scale = 10.0
        )
        distanceSlider.addChangeListener { e ->
            val value = (distanceSlider.value / 10.0f)
            distanceSliderValueLabel.text = "Camera Z: $value"
            this@BitmapViewer.cameraPositionChangeListener?.invoke(this@BitmapViewer, value)
        }

        val rotationSliderLabel = JLabel("Rotation: 0.00 radians")
        val rotationSlider = DistanceSlider(
            min = 0,
            max = (200 * Math.PI).roundToInt(),
            initial = 0,
            scale = 100.0
        )

        rotationSlider.addChangeListener { e ->
            val value = (rotationSlider.value / 100.0f)
            val format = String.format("%.2f", value)
            rotationSliderLabel.text = "Rotation: $format radians"
            this@BitmapViewer.rotationChangeListener?.invoke(this@BitmapViewer, value.toDouble())
        }


        // Create the control panel
        val controlPanel = JPanel()
        controlPanel.layout = BoxLayout(controlPanel, BoxLayout.Y_AXIS)

        val distancePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        distancePanel.add(JLabel("Camera Distance:").apply {
            this.preferredSize = Dimension(100, 20)
        })
        distancePanel.add(distanceSlider)
        distancePanel.add(distanceSliderValueLabel)
        controlPanel.add(distancePanel)

        val newPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        newPanel.add(JLabel("Rotation:").apply { this.preferredSize = Dimension(100, 20) })
        newPanel.add(rotationSlider)
        newPanel.add(rotationSliderLabel)
        controlPanel.add(newPanel)

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
}
