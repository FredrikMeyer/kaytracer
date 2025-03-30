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

class SphericalSurface(override val geometry: Sphere) : Surface {

    override val material: Material = Material(
        color = Color.RED
    )

    override fun hit(ray: Ray, interval: Interval): Point3D? {
        return ray.pointOnRay(geometry.intersect(ray)!!)
    }
}