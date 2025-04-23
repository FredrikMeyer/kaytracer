package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

/**
 * Represents a square in 3D space with finite extent.
 * @param center The center point of the square
 * @param normal The normal vector to the square (perpendicular to the square's surface)
 * @param size The side length of the square
 */
data class Square(val center: Point3D, val normal: Vector3D, val size: Float) : GeometricObject {
    // The two vectors that define the sides of the square
    private val u: Vector3D
    private val v: Vector3D

    // Half the size of the square (distance from center to edge)
    private val halfSize: Float = size / 2f

    init {
        // Normalize the normal vector
        val normalizedNormal = normal.normalize()

        // Create two vectors perpendicular to the normal and to each other
        // First, find a vector not parallel to the normal
        val notParallel = if (abs(normalizedNormal.x) < 0.9f) {
            Vector3D.X
        } else {
            Vector3D.Y
        }

        // Create the first side vector using cross product
        u = (normalizedNormal cross notParallel).normalize()

        // Create the second side vector using cross product
        v = (normalizedNormal cross u).normalize()
    }

    override fun intersect(ray: Ray, interval: Interval): Float? {
        // First check if the ray intersects the plane containing the square
        if (ray.direction dot normal == 0f) {
            return null // Ray is parallel to the plane
        }

        // Calculate the intersection parameter t
        val t =
            (normal dot (center.toVector3D() - ray.origin.toVector3D())) / (ray.direction dot normal)

        // Check if t is within the valid interval
        if (!interval.contains(t)) {
            return null
        }

        // Calculate the intersection point
        val intersectionPoint = ray.pointOnRay(t)

        // Calculate the vector from the center to the intersection point
        val toIntersection = intersectionPoint.toVector3D() - center.toVector3D()

        // Project this vector onto the u and v axes
        val uProjection = toIntersection dot u
        val vProjection = toIntersection dot v

        // Check if the intersection point is within the square's bounds
        if (abs(uProjection) <= halfSize && abs(vProjection) <= halfSize) {
            return t
        }

        return null
    }

    override fun isOnObject(point: Point3D): Boolean {
        val diff = point.toVector3D() - center.toVector3D()
        if (diff dot normal != 0f) {
            return false
        }

        val x = point.toVector3D() - center.toVector3D()

        // Project this vector onto the u and v axes
        val uProjection = diff dot u
        val vProjection = diff dot v

        // Check if the intersection point is within the square's bounds
        return abs(uProjection) <= halfSize && abs(vProjection) <= halfSize
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        return this.normal.normalize()
    }

    override fun translate(dir: Vector3D): GeometricObject {
        TODO("Not yet implemented")
    }
}

// Helper function to get absolute value
private fun abs(value: Float): Float = if (value < 0) -value else value