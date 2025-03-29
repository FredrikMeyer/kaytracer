package net.fredrikmeyer

import java.awt.Color

data class Color(val r: Float, val g: Float, val b: Float) {
    init {
        require(r in 0.0..1.0) { "r must be between 0.0 and 1.0. Actual value: $r"}
        require(g in 0.0..1.0) { "g must be between 0.0 and 1.0. Actual value: $g"}
        require(b in 0.0..1.0) { "b must be between 0.0 and 1.0. Actual value: $b"}
    }
    fun toJavaAwt(): Color {
        return Color(r, g, b)
    }
}
