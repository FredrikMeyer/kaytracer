package net.fredrikmeyer.gui

import net.fredrikmeyer.BasicBitmapStorage
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * A class for displaying the bitmap stored in BasicBitmapStorage.
 * Uses Swing to create a window that shows the image.
 */
class BitmapViewer(private val bitmapStorage: BasicBitmapStorage, title: String = "Bitmap Viewer") {
    private val frame: JFrame = JFrame(title)

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

        // Set up the frame
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.add(panel)
        frame.pack()
        frame.isResizable = false

        // Add key listener to close the frame when ESC is pressed
        frame.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    close()
                }
            }
        })
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
        }
    }

    /**
     * Refreshes the display to show any changes made to the bitmap.
     */
    fun refresh() {
        SwingUtilities.invokeLater {
            frame.repaint()
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
