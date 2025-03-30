package net.fredrikmeyer

import net.fredrikmeyer.geometry.GeometricObject
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Sphere


interface Surface {
    val geometry: GeometricObject

    val material: Material

    fun hit(
        ray: Ray,
        interval: Interval = Interval(0.0f, Float.POSITIVE_INFINITY)
    ): Point3D?
}

object SphericalSurface : Surface {
    override val geometry: GeometricObject = Sphere(
        center = Point3D(0f, 0f, 0f),
        radius = 1.0f
    )
    override val material: Material = Material(
        color = Color.RED
    )

    override fun hit(ray: Ray, interval: Interval): Point3D? {
        return geometry.intersect(ray)
    }
}