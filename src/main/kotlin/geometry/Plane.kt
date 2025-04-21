package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

data class Plane(val point: Point3D, val normal: Vector3D) : GeometricObject {
    override fun intersect(ray: Ray, interval: Interval): Float? {
        val rayDirectionDotNormal = ray.direction dot normal
        if (rayDirectionDotNormal == 0f) {
            return null
        }

        val t =
            (normal dot (point.toVector3D() - ray.origin.toVector3D())) / rayDirectionDotNormal

        return if (interval.contains(t)) t else null
    }

    override fun isOnObject(point: Point3D): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        return this.normal
    }
}