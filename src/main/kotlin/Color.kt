package net.fredrikmeyer

import kotlin.math.min
import java.awt.Color as AwtColor

data class Color(val r: Float, val g: Float, val b: Float) {
    init {
        require(r in 0.0..1.0) { "r must be between 0.0 and 1.0. Actual value: $r" }
        require(g in 0.0..1.0) { "g must be between 0.0 and 1.0. Actual value: $g" }
        require(b in 0.0..1.0) { "b must be between 0.0 and 1.0. Actual value: $b" }
    }

    fun toJavaAwt(): AwtColor {
        return AwtColor(r, g, b)
    }

    companion object {
        val WHITE = Color(1.0f, 1.0f, 1.0f)
        val RED = Color(1.0f, 0.0f, 0.0f)
        val BLUE = Color(0.0f, 0.0f, 1.0f)
        val GREEN = Color(0.0f, 1.0f, 0.0f)
        val YELLOW = Color(1.0f, 1.0f, 0.0f)
        val BLACK = Color(0.0f, 0.0f, 0.0f)
    }
}

infix operator fun Float.times(other: Color) =
    Color(min(this * other.r, 1.0f), min(this * other.g, 1.0f), min(this * other.b, 1.0f))

infix operator fun Color.plus(other: Color) =
    Color(min(this.r + other.r, 1.0f), min(this.g + other.g, 1.0f), min(this.b + other.b, 1.0f))

infix operator fun Color.times(other: Color) =
    Color(this.r * other.r, this.g * other.g, this.b * other.b)