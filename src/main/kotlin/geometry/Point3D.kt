package net.fredrikmeyer.geometry

data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun toVector3D(): Vector3D {
        return Vector3D(x, y, z)
    }
}
