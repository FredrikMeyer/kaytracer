package net.fredrikmeyer

import kotlin.math.min
import kotlin.math.pow
import java.awt.Color as AwtColor

data class Color(val r: Float, val g: Float, val b: Float) {

    fun clamp(): Color {
        return Color(r.coerceIn(0.0f, 1.0f), g.coerceIn(0.0f, 1.0f), b.coerceIn(0.0f, 1.0f))
    }

    fun toJavaAwt(): AwtColor {
        val (r,g,b) = this.clamp()
        return AwtColor(r.gammaEncode(), g.gammaEncode(), b.gammaEncode())
    }
    infix operator fun plus(other: Color) =
        Color(this.r + other.r, this.g + other.g, this.b + other.b)

    infix operator fun times(other: Color) =
        Color(this.r * other.r, this.g * other.g, this.b * other.b)

    companion object {
        val WHITE = Color(1.0f, 1.0f, 1.0f)
        val GRAY_LIGHT = Color(0.8f, 0.8f, 0.8f)
        val RED = Color(1.0f, 0.0f, 0.0f)
        val BLUE = Color(0.0f, 0.0f, 1.0f)
        val GREEN = Color(0.0f, 1.0f, 0.0f)
        val YELLOW = Color(1.0f, 1.0f, 0.0f)
        val CYAN = Color(0.0f, 1.0f, 1.0f)
        val MAGENTA = Color(1.0f, 0.0f, 1.0f)
        val BLACK = Color(0.0f, 0.0f, 0.0f)

        @Suppress("unused")
        fun allColors(): List<Color> {
            return Companion::class.members
                .filter { it.returnType == net.fredrikmeyer.Color::class }
                .mapNotNull { member ->
                    try {
                        member.call(this) as? Color
                    } catch (e: Exception) {
                        null
                    }
                }
        }


    }
}

infix operator fun Float.times(other: Color) =
    Color(min(this * other.r, 1.0f), min(this * other.g, 1.0f), min(this * other.b, 1.0f))


private const val GAMMA = 2f

private fun Float.gammaEncode(): Float {
    return this.pow(1.0f / GAMMA)
}

private fun Float.gammaDecode(): Float {
    return this.pow(GAMMA)
}

private fun Float.clamp(): Float {
    return this.coerceIn(0.0f, 1.0f)
}