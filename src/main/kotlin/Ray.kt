package net.fredrikmeyer

import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Vector3D
import net.fredrikmeyer.geometry.plus
import net.fredrikmeyer.geometry.times

data class Ray(val origin: Point3D, val direction: Vector3D) {

    fun pointOnRay(t: Float): Point3D {
        require(!t.isNaN())
        return (origin.toVector3D() + (t * direction)).toPoint3D()
    }

    fun translate(dir: Vector3D): Ray {
        return Ray(origin.translate(dir), direction)
    }
}