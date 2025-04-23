package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.linalg.Matrix3x3

class Triangle(val a: Point3D, val b: Point3D, val c: Point3D) : GeometricObject {
    private val ab = a.toVector3D() - b.toVector3D()
    private val ac = a.toVector3D() - c.toVector3D()
    private val normal = (ab cross ac).normalize()

    override fun intersect(ray: Ray, interval: Interval): Float? {
        val A = Matrix3x3(ab, ac, ray.direction)

        val detA = A.determinant()

        val ao = a.toVector3D() - ray.origin.toVector3D()
        val t = Matrix3x3(
            ab,
            ac, ao
        ).determinant() / detA

        if (!interval.contains(t)) {
            return null
        }

        val gamma = Matrix3x3(ab, ao, ray.direction).determinant() / detA

        if (gamma < 0 || gamma > 1) {
            return null
        }

        val beta = Matrix3x3(
            ao,
            ac,
            ray.direction
        ).determinant() / detA

        if (beta < 0 || beta > 1 - gamma) {
            return null
        }

        return t
    }


    override fun isOnObject(point: Point3D): Boolean {
        // First, check if the point is on the plane of the triangle
        val pointToA = point.toVector3D() - a.toVector3D()
        if (abs(pointToA dot normal) > 0.000001f) {
            return false // Point is not on the plane
        }

        // Use barycentric coordinates to determine if the point is inside the triangle
        // We can reuse the logic from the intersect method

        // Create vectors from point to vertices
        val pa = a.toVector3D() - point.toVector3D()
        val pb = b.toVector3D() - point.toVector3D()
        val pc = c.toVector3D() - point.toVector3D()

        // Compute cross products
        val crossPbPc = pb cross pc
        val crossPcPa = pc cross pa
        val crossPaPb = pa cross pb

        // Check if all cross products have the same sign as the normal
        val signPbPc = crossPbPc dot normal
        val signPcPa = crossPcPa dot normal
        val signPaPb = crossPaPb dot normal

        // All signs must be positive or all must be negative
        return (signPbPc >= 0 && signPcPa >= 0 && signPaPb >= 0) ||
                (signPbPc <= 0 && signPcPa <= 0 && signPaPb <= 0)
    }

    private fun abs(value: Float): Float = if (value < 0) -value else value

    override fun normalAtPoint(point: Point3D): Vector3D {
        return normal
    }

    override fun translate(dir: Vector3D): GeometricObject {
        return Triangle(
            a = (a.toVector3D() + dir).toPoint3D(),
            b = (b.toVector3D() + dir).toPoint3D(),
            c = (c.toVector3D() + dir).toPoint3D()
        )
    }
}
