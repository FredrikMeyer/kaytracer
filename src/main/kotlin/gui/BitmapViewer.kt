package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import net.fredrikmeyer.State
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.EtchedBorder
import javax.swing.border.TitledBorder
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
                // Get the insets of the border to position the image correctly
                val insets = border?.getBorderInsets(this) ?: Insets(0, 0, 0, 0)
                g.drawImage(bitmapStorage.image, insets.left, insets.top, this)
            }

            override fun getPreferredSize(): Dimension {
                // Get the insets of the border to add to the preferred size
                val insets = border?.getBorderInsets(this) ?: Insets(0, 0, 0, 0)
                return Dimension(
                    bitmapStorage.image.width + insets.left + insets.right,
                    bitmapStorage.image.height + insets.top + insets.bottom
                )
            }

            // Prevent the panel from expanding by making maximum size same as preferred size
            override fun getMaximumSize(): Dimension {
                return preferredSize
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
        // Add a border to the panel with appropriate padding
        panel.border =
            BorderFactory.createCompoundBorder(
                EmptyBorder(10, 10, 10, 10),
                TitledBorder(
                    EtchedBorder(),
                    "Image",
                    TitledBorder.LEFT,
                    TitledBorder.TOP
                )
            )


        val distanceSliderValueLabel = JLabel("Camera Z: ${state.currentCameraZ}")

        val distanceSlider = DistanceSlider(
            (minCameraZ * 10).toInt(),
            (maxCameraZ * 10).toInt(),
            (state.currentCameraZ * 10).toInt(),
            scale = 10.0
        )
        distanceSlider.name = "cameraZSlider"
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
        controlPanel.border =
            BorderFactory.createCompoundBorder(
                EmptyBorder(10, 10, 10, 10),
                TitledBorder(
                    EtchedBorder(),
                    "Controls",
                    TitledBorder.LEFT,
                    TitledBorder.TOP
                )
            )

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
//        frame.preferredSize = Dimension(700, 900)
        frame.layout = BorderLayout()

        val pauseButtonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val pauseButton = JButton("Pause")
        pauseButton.preferredSize = Dimension(100, 20)
        pauseButton.addActionListener {
            if (pauseButton.text == "Pause") {
                pauseButton.text = "Resume"
                state.paused = true
            } else {
                pauseButton.text = "Pause"
                state.paused = false
            }
        }
        pauseButtonPanel.add(pauseButton)
        pauseButtonPanel.border =
            BorderFactory.createCompoundBorder(
                EmptyBorder(10, 10, 10, 10),
                TitledBorder(
                    EtchedBorder(),
                    "Buttons",
                    TitledBorder.LEFT,
                    TitledBorder.TOP
                )
            )


        // Wrap the panel in a container that respects its maximum size
        val wrapperPanel = JPanel()
        wrapperPanel.layout = BoxLayout(wrapperPanel, BoxLayout.X_AXIS)
        wrapperPanel.add(Box.createHorizontalGlue())
        wrapperPanel.add(panel)
        wrapperPanel.add(pauseButtonPanel)
        wrapperPanel.add(Box.createHorizontalGlue())

        frame.add(wrapperPanel, BorderLayout.CENTER)
        frame.add(controlPanel, BorderLayout.SOUTH)
        frame.pack()
        frame.isResizable = true
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
