package net.fredrikmeyer.geometry

import net.fredrikmeyer.linalg.Matrix3x3

data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun toVector3D(): Vector3D {
        return Vector3D(x, y, z)
    }

    fun squaredDistanceTo(other: Point3D): Float {
        val (a, b, c) = other
        val dx = this.x - a
        val dy = this.y - b
        val dz = this.z - c
        return dx * dx + dy * dy + dz * dz
    }

    fun translate(dir: Vector3D): Point3D {
        return Point3D(x + dir.x, y + dir.y, z + dir.z)
    }

    fun apply(matrix3x3: Matrix3x3): Point3D {
        val vector = matrix3x3 * this.toVector3D()
        return Point3D(vector.x, vector.y, vector.z)
    }
}
