package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

data class Plane(val point: Point3D, val normal: Vector3D) : GeometricObject {
    override fun intersect(ray: Ray, interval: Interval): Float? {
        if (ray.direction dot normal == 0f) {
            return null
        }

        val t =
            (normal dot (point.toVector3D() - ray.origin.toVector3D())) / (ray.direction dot normal)

        return if (interval.contains(t)) t else null
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        return this.normal
    }
}