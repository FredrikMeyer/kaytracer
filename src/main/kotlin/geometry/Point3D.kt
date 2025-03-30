package net.fredrikmeyer.geometry

data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun toVector3D(): Vector3D {
        return Vector3D(x, y, z)
    }

    fun squaredDistanceTo(other: Point3D): Float {
        val (a, b, c) = other
        val dx = this.x - a
        val dy = this.y - b
        val dz = this.z - c
        return dx * dx + dy * dy + dz * dz
    }
}
