package net.fredrikmeyer.geometry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fredrikmeyer.*
import java.lang.Math.random
import kotlin.math.max
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RayTracer(private val width: Int, private val height: Int, private val scene: Scene) {
    fun doRayTracing(): Map<Pair<Int, Int>, Color> = runBlocking {
        val currentInstant = Clock.System.now()
        val chunkLength = width / 4

        val pixels = Array(height) { Array(width) { Color.BLACK } }

        val jobs = (0..<width).chunked(chunkLength).map { chunk ->
            launch(Dispatchers.Default) {
                for (x in chunk) {
                    for (y in 0..<height) {
                        var c = Color.BLACK
                        val level = 5
                        for (p in 0..<level) {
                            for (q in 0..<level) {
                                c += getColorAtPixel(
                                    (x + (p + random()) / level).toFloat(),
                                    (y + (q + random()) / level).toFloat(),
                                )
                            }
                        }
                        c = (1f / (level * level)) * c
                        pixels[y][x] = c
                    }
                }
            }
        }
        jobs.joinAll()

        val res = mutableMapOf<Pair<Int, Int>, Color>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                res[Pair(x, y)] = pixels[y][x]
            }
        }

        val duration = Clock.System.now() - currentInstant
        println("Done drawing pixels. Took: $duration")

        res
    }

    private fun colorOfRay(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY),
        recursionDepth: Int = 0,
    ): Color {
        if (recursionDepth >= MAX_RECURSION_DEPTH) {
            return Color.BLACK
        }

        return scene.hit(ray, interval = interval)?.let {
            val point = it.point
            val normal = it.surface.geometry.normalAtPoint(point)
            val lightPos = scene.lightPosition
            val lightDir = (lightPos.toVector3D() - point.toVector3D()).normalize()

            // Ambient light
            val ka = it.surface.material.color
            val Iambient = scene.ambientLightIntensity
            val ambientIntensity = Iambient * ka;

            val rayFromHit = Ray(point, lightDir)
            val res = scene.hit(rayFromHit, Interval(0.00001f, Float.POSITIVE_INFINITY))

            val shadowContribution = if (res == null) {
                val I = 2.0f
                val kd = it.surface.material.color
                val lambertian = max(0f, normal dot lightDir) * kd

                val vv = -1f * ray.direction
                val h = (vv + lightDir).normalize()
                val ks = Color.WHITE
                val phong = max(0.0, (normal dot h).toDouble()).pow(100.0).toFloat() * ks

                I * (lambertian + phong)
            } else {
                Color.BLACK
            }
            val reflectionVector = createReflectionVector(ray, normal)
            val newRay = Ray(point, reflectionVector)
            val reflectivity = it.surface.material.reflectivity
            val color =
                (1 - reflectivity) * (ambientIntensity + shadowContribution) + reflectivity * colorOfRay(
                    newRay,
                    interval = Interval(0.0001f, Float.POSITIVE_INFINITY),
                    recursionDepth + 1
                )
            return color
        } ?: Color.BLACK
    }

    private fun createReflectionVector(ray: Ray, normal: Vector3D): Vector3D {
        return ray.direction - 2f * (ray.direction dot normal) * normal
    }

    private fun getColorAtPixel(
        x: Float,
        y: Float,
    ): Color {
        val uvCoordinates = pixelToUV(
            x,
            y,
            ImagePlane(-0.5f, 0.5f, -0.5f, 0.5f)
        )
        val ray = rayAtPoint(uvCoordinates.first, uvCoordinates.second)
        val color = colorOfRay(
            ray = ray
        )
        return color
    }

    private fun rayAtPoint(u: Float, v: Float): Ray {
        val uu = Vector3D(1f, 0f, 0f)
        val vv = Vector3D(0f, 1f, 0f)
        val w = Vector3D(0f, 0f, 1f)

        val e = Point3D(0f, 0f, currentCameraZ)

        val d = 1f
        val ray = Ray(
            origin = e,
            direction = (-d * w + (u * uu + v * vv)).normalize()
        )
        return ray
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