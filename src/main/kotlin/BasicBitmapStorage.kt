package net.fredrikmeyer

import java.awt.Color
import java.awt.image.BufferedImage

class BasicBitmapStorage(width: Int, private val height: Int) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

    fun fill(c: Color) {
        val g = image.graphics
        g.color = c
        g.fillRect(0, 0, image.width, image.height)
    }

    fun setPixels(colors: Map<Pair<Int, Int>, net.fredrikmeyer.Color>) {
        colors.forEach { (p, c) ->
            setPixel(p.first, p.second, c.toJavaAwt())
        }
    }

    fun setPixel(x: Int, y: Int, c: Color) {
        require(x < image.width && y < image.height)
        require(x >= 0 && y >= 0)
        image.setRGB(x, height - 1 - y, c.rgb)
    }

    fun getPixel(x: Int, y: Int) = Color(image.getRGB(x, y))
}