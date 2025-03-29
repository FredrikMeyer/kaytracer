package net.fredrikmeyer

import kotlin.math.sqrt

data class Ray(val origin: Point3D, val direction: Vector3D) {

    fun pointOnRay(t: Float): Point3D {
        return (origin.toVector3D() + (t * direction)).toPoint3D()
    }
}

fun intersectRaySphere(ray: Ray, sphere: Sphere): Float? {
    val dirToSphere = ray.origin.toVector3D() - sphere.center.toVector3D()
    val direction = ray.direction
    val radius = sphere.radius
    val discriminant =
        (direction dot dirToSphere).squared() - (direction dot direction) * ((dirToSphere dot dirToSphere) - radius.squared())

    if (discriminant < 0) {
        return null
    }

    return (-(direction dot dirToSphere) - sqrt(discriminant)) / (direction dot direction)
}