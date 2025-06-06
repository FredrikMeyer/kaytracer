package net.fredrikmeyer.geometry

import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    init {
        if (x.isNaN() || y.isNaN() || z.isNaN()) {
            throw IllegalArgumentException("Vector3D cannot contain NaN values")
        }
    }

    fun normalize(): Vector3D {
        val norm = this.norm()
        if (norm == 0f) {
            return this
        }
        return Vector3D(x / norm, y / norm, z / norm)
    }

    fun norm() = sqrt(this.dot(this))

    fun toPoint3D(): Point3D {
        return Point3D(x, y, z)
    }

    infix fun cross(other: Vector3D): Vector3D {
        val (a, b, c) = other
        return Vector3D(c * y - b * z, a * z - c * x, b * x - a * y)
    }

    infix fun dot(other: Vector3D): Float {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }

    infix operator fun minus(other: Vector3D) =
        Vector3D(this.x - other.x, this.y - other.y, this.z - other.z)


    fun round(): Vector3D {
        return Vector3D(
            x.roundToInt().toFloat(),
            y.roundToInt().toFloat(),
            z.roundToInt().toFloat()
        )
    }

    companion object {
        val ZERO = Vector3D(0f, 0f, 0f)
        val X = Vector3D(1f, 0f, 0f)
        val Y = Vector3D(0f, 1f, 0f)
        val Z = Vector3D(0f, 0f, 1f)

        fun random(): Vector3D {
            return Vector3D(
                Math.random().toFloat(),
                Math.random().toFloat(),
                Math.random().toFloat()
            )
        }
    }
}


infix operator fun Vector3D.plus(other: Vector3D) =
    Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)

operator fun Vector3D.unaryMinus() =
    Vector3D(-this.x, -this.y, -this.z)

infix operator fun Float.times(other: Vector3D) =
    Vector3D(this * other.x, this * other.y, this * other.z)

fun Vector3D.normSquared() = this.dot(this)