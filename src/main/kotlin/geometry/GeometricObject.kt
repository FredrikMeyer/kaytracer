package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.linalg.Matrix3x3

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

    fun apply(matrix3x3: Matrix3x3): GeometricObject {
        return object : GeometricObject {
            override fun intersect(ray: Ray, interval: Interval): Float? {
                // Skal være inversen til matrisen her
                return this.intersect(Ray(ray.origin.apply(matrix3x3), TODO()), interval)
            }

            override fun isOnObject(point: Point3D): Boolean {
                return this.isOnObject(point.apply(matrix3x3))
            }

            override fun normalAtPoint(point: Point3D): Vector3D {
                return this.normalAtPoint(point.apply(matrix3x3))
            }
        }
    }
}

