package net.fredrikmeyer

import net.fredrikmeyer.geometry.GeometricObject
import net.fredrikmeyer.geometry.Plane
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Sphere


interface Surface {
    val geometry: GeometricObject

    val material: Material
}

class SphericalSurface(override val geometry: Sphere, override val material: Material) : Surface {
}

class PlanarSurface(override val geometry: Plane, override val material: Material) : Surface {

}