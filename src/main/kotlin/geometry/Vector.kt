package net.fredrikmeyer.geometry

import kotlin.math.sqrt

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    fun normalize(): Vector3D {
        val norm = this.norm()
        if (norm == 0f) {
            return this
        }
        return Vector3D(x / norm, y / norm, z / norm)
    }

    fun toPoint3D(): Point3D {
        return Point3D(x, y, z)
    }
}


infix operator fun Vector3D.plus(other: Vector3D) =
    Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)

infix operator fun Vector3D.minus(other: Vector3D) =
    Vector3D(this.x - other.x, this.y - other.y, this.z - other.z)

infix fun Vector3D.dot(other: Vector3D) =
    this.x * other.x + this.y * other.y + this.z * other.z

infix operator fun Float.times(other: Vector3D) =
    Vector3D(this * other.x, this * other.y, this * other.z)

fun Vector3D.norm() = sqrt(this.dot(this))