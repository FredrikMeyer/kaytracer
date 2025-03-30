package net.fredrikmeyer.geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray

data class Hit(
    val distance: Float,
    val normal: Vector3D,
    val point: Point3D,
    val geometricObject: GeometricObject
)

class Scene(private val objects: List<GeometricObject>) {
    fun hit(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY)
    ): Hit? {
        val closestObject =
            objects
                .mapNotNull { obj -> obj.intersect(ray, interval)?.let { Pair(obj, it) } }
                .minByOrNull { it.second }

        if (closestObject == null) {
            return null
        }

        val distance = closestObject.second
        return Hit(
            distance,
            closestObject.first.normalAtPoint(ray.pointOnRay(distance)),
            ray.pointOnRay(distance),
            closestObject.first
        )
    }
}