package net.fredrikmeyer.geometry

import net.fredrikmeyer.Ray

class Camera(var seeFrom: Point3D, val lookAt: Point3D) {
    fun ray(uv: Pair<Float, Float>): Ray {
        val (u, v) = uv

        val up = Vector3D(0f, 1f, 0f)
        val dir = (lookAt.toVector3D() - seeFrom.toVector3D()).normalize()

        val uu = -1f * (up cross dir)

        val vv = up
        val w = Vector3D(0f, 0f, 1f)

        val e = seeFrom

        val d = 1f
        return Ray(
            origin = e,
            direction = (d * dir + (u * uu + v * vv)).normalize()
        )
    }
}