package net.fredrikmeyer

import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Vector3D

class Camera(val width: Int, val height: Int, val location: Point3D) {
    val u = Vector3D(1f, 0f, 0f)
    val v = Vector3D(0f, 1f, 0f)
    val direction = Vector3D(0f, 0f, 1f)
}