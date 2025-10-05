package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

interface GeometricObject {
    fun intersect(
        ray: Ray,
        interval: Interval = Interval(-0.0001f, Float.POSITIVE_INFINITY)
    ): Float?

    fun isOnObject(point: Point3D): Boolean

    fun normalAtPoint(point: Point3D): Vector3D

    fun translate(dir: Vector3D): GeometricObject {
        return object : GeometricObject {
            override fun intersect(ray: Ray, interval: Interval): Float? {
                return this.intersect(ray.translate(dir), interval)
            }

            override fun isOnObject(point: Point3D): Boolean {
                return this.isOnObject(point.translate(-dir))
            }

            override fun normalAtPoint(point: Point3D): Vector3D {
                return this.normalAtPoint(point.translate(-dir))
            }
        }
    }

}

