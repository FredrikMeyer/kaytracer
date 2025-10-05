package net.fredrikmeyer

data class Material(
    val color: Color,
    val specularCoefficient: Color = Color.GRAY_LIGHT,
    val phongCoefficient: Double = 100.0,
    val reflectivity: Float = 0.3f
)
