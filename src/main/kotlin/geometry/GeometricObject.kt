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
}

class UnionOfGeometricObjects(val obj1: GeometricObject, val obj2: GeometricObject) :
    GeometricObject {
    override fun intersect(ray: Ray, interval: Interval): Float? {
        val intersection1 = obj1.intersect(ray, interval)
        val intersection2 = obj2.intersect(ray, interval)

        if (intersection1 == null) {
            return intersection2
        }
        if (intersection2 == null) {
            return intersection1
        }
        return minOf(intersection1, intersection2)
    }

    override fun isOnObject(point: Point3D): Boolean {
        return obj1.isOnObject(point) || obj2.isOnObject(point)
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        if (obj1.isOnObject(point)) {
            return obj1.normalAtPoint(point)
        } else {
            return obj2.normalAtPoint(point)
        }

    }

}
