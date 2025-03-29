package net.fredrikmeyer


data class Sphere(val center: Point3D, val radius: Float) {
    fun normalAtPoint(point: Point3D): Vector3D {
        return (point.toVector3D() - center.toVector3D()).normalize()
    }
}
