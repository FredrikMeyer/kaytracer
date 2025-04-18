package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.squared
import kotlin.math.abs
import kotlin.math.sqrt

interface GeometricObject {
    fun intersect(
        ray: Ray,
        interval: Interval = Interval(-0.0001f, Float.POSITIVE_INFINITY)
    ): Float?

    fun normalAtPoint(point: Point3D): Vector3D
}

data class Sphere(val center: Point3D, val radius: Float) : GeometricObject {
    override fun normalAtPoint(point: Point3D): Vector3D {
        return (point.toVector3D() - center.toVector3D()).normalize()
    }

    override fun intersect(ray: Ray, interval: Interval): Float? {
        return intersectRaySphere(ray, this)
            ?.let { if (interval.contains(it)) it else null }
    }

    private fun intersectRaySphere(ray: Ray, sphere: Sphere): Float? {
        val dirToSphere = ray.origin.toVector3D() - sphere.center.toVector3D()
        val direction = ray.direction
        val radius = sphere.radius
        val discriminant =
            (direction dot dirToSphere).squared() - (direction dot direction) * ((dirToSphere dot dirToSphere) - radius.squared())

        if (discriminant < 0) {
            return null
        }

        return (-(direction dot dirToSphere) - sqrt(discriminant)) / (direction dot direction)
    }

    fun isOnSphere(point: Point3D): Boolean {
        return abs((point.toVector3D() - center.toVector3D()).norm() - radius) < 0.000001
    }
}


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