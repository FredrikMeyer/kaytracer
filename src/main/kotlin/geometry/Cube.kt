package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

/**
 * An axis-aligned cube, defined by [cornerNear] and [cornerFar].
 *
 * TODO: Maybe implement [this](https://www.google.com/search?q=An+Efficient+and+Robust+Ray%E2%80%93Box+Intersection+Algorithm+by+Amy+Williams+et+al.%2C+2004.&oq=An+Efficient+and+Robust+Ray%E2%80%93Box+Intersection+Algorithm+by+Amy+Williams+et+al.%2C+2004.&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQIRiPAjIHCAIQIRiPAjIHCAMQIRiPAtIBBzE0N2owajeoAgCwAgA&sourceid=chrome&ie=UTF-8)
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

        // https://github.com/JustThinkInC/Raytracer/blob/master/Cube.cpp
        val tx1 = (cornerNear.x - origin.x) / dir.x
        val tx2 = (cornerFar.x - origin.x) / dir.x
        val ty1 = (cornerNear.y - origin.y) / dir.y
        val ty2 = (cornerFar.y - origin.y) / dir.y
        val tz1 = (cornerNear.z - origin.z) / dir.z
        val tz2 = (cornerFar.z - origin.z) / dir.z

        val tNear = maxOf(minOf(tx1, tx2), minOf(ty1, ty2), minOf(tz1, tz2))
        val tFar = minOf(maxOf(tx1, tx2), maxOf(ty1, ty2), maxOf(tz1, tz2))

        if (tNear > tFar || tFar < 0) {
            return null
        }

        return tNear.coerceIn(interval) //?
    }

    override fun isOnObject(point: Point3D): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        val toCenter = (point.toVector3D() - center).normalize().round()

        return toCenter
    }

    override fun translate(dir: Vector3D): GeometricObject {
        return Cube(
            (cornerNear.toVector3D() + dir).toPoint3D(),
            (cornerFar.toVector3D() + dir).toPoint3D()
        )
    }
}