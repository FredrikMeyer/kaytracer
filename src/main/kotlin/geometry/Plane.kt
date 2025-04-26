package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

data class Plane(val point: Point3D, val normal: Vector3D) : GeometricObject {
    private val pointVector = point.toVector3D()
    override fun intersect(ray: Ray, interval: Interval): Float? {
        val rayDirectionDotNormal = ray.direction dot normal
        if (rayDirectionDotNormal == 0f) {
            return null
        }

        val t =
            (normal dot (pointVector - ray.origin.toVector3D())) / rayDirectionDotNormal

        return if (interval.contains(t)) t else null
    }

    override fun isOnObject(point: Point3D): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        return this.normal
    }

    override fun translate(dir: Vector3D): GeometricObject {
        return Plane(
            (pointVector + dir).toPoint3D(),
            normal
        )
    }
}