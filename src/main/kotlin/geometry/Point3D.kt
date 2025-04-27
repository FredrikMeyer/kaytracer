package net.fredrikmeyer.geometry

import net.fredrikmeyer.linalg.Matrix3x3

data class Point3D(val x: Float, val y: Float, val z: Float) {
    init {
        if (x.isNaN() || y.isNaN() || z.isNaN()) {
            throw IllegalArgumentException("Point3D cannot contain NaN values")
        }

    }

    constructor(x: Double, y: Double, z: Double) : this(x.toFloat(), y.toFloat(), z.toFloat())
    constructor(x: Int, y: Int, z: Int) : this(x.toFloat(), y.toFloat(), z.toFloat())

    private val vector = Vector3D(x, y, z)

    fun toVector3D(): Vector3D {
        return vector
    }

    fun translate(dir: Vector3D): Point3D {
        return Point3D(x + dir.x, y + dir.y, z + dir.z)
    }

    fun apply(matrix3x3: Matrix3x3): Point3D {
        val vector = matrix3x3 * this.toVector3D()
        return Point3D(vector.x, vector.y, vector.z)
    }
}
