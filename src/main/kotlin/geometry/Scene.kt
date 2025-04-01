package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.Surface

data class Hit(
    val distance: Float,
    val point: Point3D,
    val surface: Surface
)

class Scene(private val objects: List<Surface>) {
    fun hit(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY)
    ): Hit? {
        val closestObject =
            objects
                .mapNotNull { obj -> obj.geometry.intersect(ray, interval)?.let { Pair(obj, it) } }
                .minByOrNull { it.second }

        if (closestObject == null) {
            return null
        }

        val (obj, distance) = closestObject

        val point = ray.pointOnRay(distance)

        return Hit(
            distance,
            point,
            obj
        )
    }
}