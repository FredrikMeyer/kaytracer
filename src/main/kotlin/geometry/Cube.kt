package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

/**
 * An axis-aligned cube, defined by [cornerNear] and [cornerFar].
 */
data class Cube(val cornerNear: Point3D, val cornerFar: Point3D) : GeometricObject {
    init {
        require(cornerNear.x < cornerFar.x)
        require(cornerNear.y < cornerFar.y)
        require(cornerNear.z < cornerFar.z)
    }

    private val center = 0.5f * (cornerNear.toVector3D() + cornerFar.toVector3D())

    override fun intersect(ray: Ray, interval: Interval): Float? {
        val dir = ray.direction
        val origin = ray.origin
        val intersectTop = (cornerFar.z - origin.z) / dir.z

        if (!interval.contains(intersectTop)) {
            return null
        }
        val intersectionPoint = ray.pointOnRay(intersectTop)
        if (intersectionPoint.x < cornerNear.x
            || intersectionPoint.x > cornerFar.x
            || intersectionPoint.y < cornerNear.y
            || intersectionPoint.y > cornerFar.y
            || intersectionPoint.z < cornerNear.z
            || intersectionPoint.z > cornerFar.z
        ) {
            return null
        } else {
            return intersectTop
        }

        val intersectBottom = (cornerNear.z - origin.z) / dir.z


    }

    override fun isOnObject(point: Point3D): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        val toCenter = (point.toVector3D() - center).normalize().round()

        return toCenter
    }
}