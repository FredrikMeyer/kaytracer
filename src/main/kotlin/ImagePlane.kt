package net.fredrikmeyer

data class ImagePlane(
    val left: Float,
    private val right: Float,
    val bottom: Float,
    private val top: Float,
) {
    fun width(): Float {
        return right - left
    }

    fun height(): Float {
        return top - bottom
    }
}