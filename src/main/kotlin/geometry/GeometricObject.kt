package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

interface GeometricObject {
    fun intersect(
        ray: Ray,
        interval: Interval = Interval(-0.0001f, Float.POSITIVE_INFINITY)
    ): Float?

    fun normalAtPoint(point: Point3D): Vector3D
}