package net.fredrikmeyer.geometry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import net.fredrikmeyer.*
import java.lang.Math.random
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

//import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RayTracer(
    private val width: Int,
    private val height: Int,
    private val scene: Scene,
    private val maxRecursionDepth: Int = 3,
    private val antiAliasMaxLevel: Int = 10
) {
    private val rayTracingExecutor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ).asCoroutineDispatcher()


    fun doRayTracing(): Map<Pair<Int, Int>, Color> = runBlocking {
        val currentInstant = Clock.System.now()
        val chunkLength = width / 4
        println("Available processors: ${Runtime.getRuntime().availableProcessors()}")
        val tileSize = width / 2 // Typically 16x16, 32x32 or 64x64 works well

        // Create work tiles
        val tiles = mutableListOf<Pair<IntRange, IntRange>>()
        for (y in 0..<height step tileSize) {
            val yEnd = min(y + tileSize, height)
            for (x in 0..<width step tileSize) {
                val xEnd = min(x + tileSize, width)
                tiles.add(Pair(x..<xEnd, y..<yEnd))
            }
        }

        println("Tiles: ${tiles.size}")
        val pixels = Array(height) { Array(width) { Color.BLACK } }


        val jobs = tiles.map { (xRange, yRange) ->
            async(rayTracingExecutor) {
                for (y in yRange) {
                    for (x in xRange) {

                        var c = Color.BLACK
                        val level = antiAliasMaxLevel
                        for (p in 0..<level) {
                            for (q in 0..<level) {
                                val jitteredX = (x + (p + random()) / level).toFloat()
                                val jitteredY = (y + (q + random()) / level).toFloat()

                                c += getColorAtPixel(jitteredX, jitteredY)
                            }
                        }
                        c = (1f / (level * level)) * c
                        pixels[y][x] = c
                    }
                }
                println("Completed tile ${xRange.first}x${yRange.first} at ${Clock.System.now() - currentInstant}")
            }
        }
        jobs.awaitAll()

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

    private tailrec fun colorOfRay(
        ray: Ray,
        accumulatedColor: Color = Color.BLACK,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY),
        recursionDepth: Int = 0,
    ): Color {
        if (recursionDepth >= maxRecursionDepth) {
            return accumulatedColor
        }

        val hit = scene.hit(ray, interval = interval)
        if (hit == null) {
            return accumulatedColor
        }

        val hitPoint = hit.point
        val material = hit.surface.material

        // Ambient light
        // k_a
        val materialColor = material.color
        var color = scene.ambientLightIntensity * materialColor

        val normal = hit.surface.geometry.normalAtPoint(hitPoint)
        val lightSource = scene.getLightSource()
        val lightPos = lightSource.position
        val lightDir = (lightPos.toVector3D() - hitPoint.toVector3D()).normalize()

        val rayFromHitToLight = Ray(hitPoint, lightDir)

        // Compute if the ray from the hit point to the light source intersects with any object
        val shadowIntersection =
            scene.hit(rayFromHitToLight, Interval(0.00001f, Float.POSITIVE_INFINITY))

        color = color + if (shadowIntersection == null) {
            val distanceToLight = (lightPos.toVector3D() - hitPoint.toVector3D()).normSquared()
            // Scale intensity with inverse square law
            val I = (lightSource.intensity / (4 * PI * distanceToLight)).toFloat()
            val kd = materialColor
            val lambertian = max(0f, normal dot lightDir) * kd

            val vv = -1f * ray.direction
            val h = (vv + lightDir).normalize()
            val ks = material.specularCoefficient
            val phong = max(0.0, (normal dot h).toDouble()).pow(100.0).toFloat() * ks

            I * (lambertian + phong)
        } else {
            Color.BLACK
        }
        val reflectionVector = createReflectionVector(ray, normal)
        val newRay = Ray(hitPoint, reflectionVector)
        val reflectivity = material.reflectivity

        val acc = accumulatedColor + (1 - reflectivity) * color
        return colorOfRay(
            newRay,
            accumulatedColor = acc,
            interval = Interval(0.0001f, Float.POSITIVE_INFINITY),
            recursionDepth + 1
        )
    }

    private fun createReflectionVector(ray: Ray, normal: Vector3D): Vector3D {
        return ray.direction - 2f * (ray.direction dot normal) * normal
    }

    private fun pixelToUV(
        i: Float,
        j: Float,
        imagePlane: ImagePlane,
    ): Pair<Float, Float> {
        val (l, r, b, t) = imagePlane
        val width = r - l
        val height = t - b
        val u = l + width * (i + 0.5f) / this.width
        val v = b + height * (j + 0.5f) / this.height
        return Pair(u, v)
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
        val pointInImagePlane = u * uu + v * vv

        val ray = Ray(
            origin = e,
            direction = (-d * w + pointInImagePlane).normalize()
        )
        return ray
    }

}