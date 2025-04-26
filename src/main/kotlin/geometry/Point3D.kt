package net.fredrikmeyer.geometry

data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun toVector3D(): Vector3D {
        return Vector3D(x, y, z)
    }

    fun translate(dir: Vector3D): Point3D {
        return Point3D(x + dir.x, y + dir.y, z + dir.z)
    }
}
