package net.fredrikmeyer

import net.fredrikmeyer.geometry.*


interface Surface {
    val geometry: GeometricObject

    val material: Material
}

class UnionOfSpheres(val sphere1: Sphere, val sphere2: Sphere) : Surface {
    override val geometry: GeometricObject = object : GeometricObject {
        override fun intersect(ray: Ray, interval: Interval): Float? {
            val intersection1 = sphere1.intersect(ray, interval)
            val intersection2 = sphere2.intersect(ray, interval)

            if (intersection1 == null) {
                return intersection2
            }
            if (intersection2 == null) {
                return intersection1
            }
            return minOf(intersection1, intersection2)
        }

        override fun normalAtPoint(point: Point3D): Vector3D {
            if (sphere1.isOnSphere(point)) {
                return sphere1.normalAtPoint(point)
            } else if (sphere2.isOnSphere(point)) {
                return sphere2.normalAtPoint(point)
            }
            error("Must be on sphere. Point: $point. Distance from sphere1: ${(sphere1.center.toVector3D() - point.toVector3D()).norm()}")
        }

    }
    override val material: Material = Material(
        color = Color.YELLOW,
        reflectivity = 0.0f
    )
}

class IntersectionOfSpheres(val sphere1: Sphere, val sphere2: Sphere) : Surface {
    override val geometry: GeometricObject = object : GeometricObject {
        override fun intersect(ray: Ray, interval: Interval): Float? {
            val intersection1 = sphere1.intersect(ray, interval)
            val intersection2 = sphere2.intersect(ray, interval)

            if (intersection1 == null || intersection2 == null) {
                return null
            }

            return maxOf(intersection1, intersection2)
        }

        override fun normalAtPoint(point: Point3D): Vector3D {
            if (sphere1.isOnSphere(point)) {
                return sphere1.normalAtPoint(point)
            } else if (sphere2.isOnSphere(point)) {
                return sphere2.normalAtPoint(point)
            }
            error("Must be on sphere. Point: $point. Distance from sphere1: ${(sphere1.center.toVector3D() - point.toVector3D()).norm()}")
        }

    }
    override val material: Material = Material(
        color = Color.YELLOW,
        reflectivity = 0.0f
    )
}