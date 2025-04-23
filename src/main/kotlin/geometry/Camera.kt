package net.fredrikmeyer.geometry

import net.fredrikmeyer.ImagePlane
import net.fredrikmeyer.Ray
import net.fredrikmeyer.currentCameraZ

class Camera(val seeFrom: Point3D, private val width: Int, private val height: Int) {
    fun ray(lookAt: Point3D): Ray {
        val uu = Vector3D(1f, 0f, 0f)
        val vv = Vector3D(0f, 1f, 0f)
        val w = Vector3D(0f, 0f, 1f)

        val e = Point3D(0f, 0f, currentCameraZ)

        val d = 1f
        val ray = Ray(
            origin = e,
            direction = TODO() //(-d * w + (u * uu + v * vv)).normalize()
        )
    }

    private fun pixelToUV(
        i: Float,
        j: Float,
        imagePlane: ImagePlane,
    ): Pair<Float, Float> {
        val (l, r, b, t) = imagePlane
        val width = r - l
        val height = t - b
        val u = l + width * (i + 0.5f) / this.width;
        val v = b + height * (j + 0.5f) / this.height;
        return Pair(u, v)
    }
}