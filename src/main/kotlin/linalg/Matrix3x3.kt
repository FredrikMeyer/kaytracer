package net.fredrikmeyer.linalg

import net.fredrikmeyer.geometry.Vector3D
import net.fredrikmeyer.geometry.plus

/**
 * A matrix looking like
 * ```
 * a11 a21 a31
 * a12 a22 a32
 * a13 a23 a33
 * ```
 */
class Matrix3x3(
    a11: Float,
    a12: Float,
    a13: Float,
    a21: Float,
    a22: Float,
    a23: Float,
    a31: Float,
    a32: Float,
    a33: Float
) {
    private val cs =
        listOf(Vector3D(a11, a12, a13), Vector3D(a21, a22, a23), Vector3D(a31, a32, a33))

    companion object {
        fun identity(): Matrix3x3 {
            return Matrix3x3(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        }
    }

    constructor(c1: Vector3D, c2: Vector3D, c3: Vector3D) : this(
        c1.x,
        c1.y,
        c1.z,
        c2.x,
        c2.y,
        c2.z,
        c3.x,
        c3.y,
        c3.z
    )

    operator fun get(row: Int): Vector3D {
        return cs[row]
    }

    fun transpose(): Matrix3x3 {
        return Matrix3x3(
            Vector3D(cs[0].x, cs[1].x, cs[2].x),
            Vector3D(cs[0].y, cs[1].y, cs[2].y),
            Vector3D(cs[0].z, cs[1].z, cs[2].z)
        )
    }

    fun determinant(): Float {
        return cs[0].x * (cs[1].y * cs[2].z - cs[2].y * cs[1].z) -
                cs[1].x * (cs[0].y * cs[2].z - cs[2].y * cs[0].z) +
                cs[2].x * (cs[0].y * cs[1].z - cs[1].y * cs[0].z)
    }

    infix operator fun times(other: Vector3D): Vector3D {
        val t = this.transpose()
        return Vector3D(t[0] dot other, t[1] dot other, t[2] dot other)
    }

    infix operator fun times(other: Matrix3x3): Matrix3x3 {
        return Matrix3x3(this * other[0], this * other[1], this * other[2])
    }

    infix operator fun plus(other: Matrix3x3): Matrix3x3 {
        return Matrix3x3(cs[0] + other[0], cs[1] + other[1], cs[2] + other[2])
    }

    infix operator fun minus(other: Matrix3x3): Matrix3x3 {
        return Matrix3x3(cs[0] - other[0], cs[1] - other[1], cs[2] - other[2])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix3x3

        return cs == other.cs
    }

    override fun hashCode(): Int {
        return cs.hashCode()
    }

    override fun toString(): String {
        return "Matrix3x3(cs=$cs)"
    }
}