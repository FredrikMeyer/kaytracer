package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import java.lang.Math.random

/**
 * An axis-aligned cube, defined by [cornerNear] and [cornerFar].
 *
 * TODO: Maybe implement [this](https://www.google.com/search?q=An+Efficient+and+Robust+Ray%E2%80%93Box+Intersection+Algorithm+by+Amy+Williams+et+al.%2C+2004.&oq=An+Efficient+and+Robust+Ray%E2%80%93Box+Intersection+Algorithm+by+Amy+Williams+et+al.%2C+2004.&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQIRiPAjIHCAIQIRiPAjIHCAMQIRiPAtIBBzE0N2owajeoAgCwAgA&sourceid=chrome&ie=UTF-8)
 * https://blog.johnnovak.net/2016/10/22/the-nim-ray-tracer-project-part-4-calculating-box-normals/
 */
data class Cube(val cornerNear: Point3D, val cornerFar: Point3D) : GeometricObject {
    init {
        require(cornerNear.x < cornerFar.x)
        require(cornerNear.y < cornerFar.y)
        require(cornerNear.z < cornerFar.z)
    }

    private val center = 0.5f * (cornerNear.toVector3D() + cornerFar.toVector3D())
    private val dx = 0.5f * abs(cornerFar.x - cornerNear.x)
    private val dy = 0.5f * abs(cornerFar.y - cornerNear.y)
    private val dz = 0.5f * abs(cornerFar.z - cornerNear.z)

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

        return if (interval.contains(tNear)) tNear else null
    }

    override fun isOnObject(point: Point3D): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalAtPoint(point: Point3D): Vector3D {
        val toCenter = (point.toVector3D() - center)
        val bias = 1.0001f
        val n =
            Vector3D(
                (bias * toCenter.x / abs(dx)).toInt().toFloat(),
                (bias * toCenter.y / abs(dy)).toInt().toFloat(),
                (bias * toCenter.z / abs(dz)).toInt().toFloat()
            )
        return n.normalize()
    }

    override fun translate(dir: Vector3D): GeometricObject {
        return Cube(
            (cornerNear.toVector3D() + dir).toPoint3D(),
            (cornerFar.toVector3D() + dir).toPoint3D()
        )
    }
}