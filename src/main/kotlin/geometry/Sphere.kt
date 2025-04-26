package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.squared
import kotlin.math.abs
import kotlin.math.sqrt

data class Sphere(val center: Point3D, val radius: Float) : GeometricObject {
    override fun normalAtPoint(point: Point3D): Vector3D {
        return (point.toVector3D() - center.toVector3D()).normalize()
    }

    override fun intersect(ray: Ray, interval: Interval): Float? {
        return intersectRaySphere(ray, this)
            ?.let { if (interval.contains(it)) it else null }
    }

    override fun isOnObject(point: Point3D): Boolean {
        return abs((point.toVector3D() - center.toVector3D()).norm() - radius) < 0.000001
    }

    private fun intersectRaySphere(ray: Ray, sphere: Sphere): Float? {
        val dirToSphere = ray.origin.toVector3D() - sphere.center.toVector3D()
        val direction = ray.direction
        val radius = sphere.radius
        // dir.dirToSpehere - (|d|^2|dirToSphere]^2 - r^2)
        val f = direction dot dirToSphere
        val discriminant =
            f.squared() - (direction.normSquared()) * (dirToSphere.normSquared() - radius.squared())

        if (discriminant < 0) {
            return null
        }

        return (-f - sqrt(discriminant)) / (direction.normSquared())
    }

    fun isOnSphere(point: Point3D): Boolean {
        return this.isOnObject(point)
    }

    override fun translate(dir: Vector3D): Sphere {
        return Sphere((center.toVector3D() + dir).toPoint3D(), radius)
    }

    fun scale(by: Float): Sphere {
        return Sphere(center, radius * by)
    }
}
