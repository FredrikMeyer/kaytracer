package net.fredrikmeyer.geometry

import net.fredrikmeyer.*
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.time.ExperimentalTime

data class RayTracerConfig(
    val width: Int = 700,
    val height: Int = 700,
    val maxRecursionDepth: Int = 3,
    val antiAliasMaxLevel: Int = 3
)

@OptIn(ExperimentalTime::class)
class RayTracer(
    private val scene: Scene,
    private val config: RayTracerConfig,
    val camera: Camera
) {
    private val width = config.width
    private val height = config.height

    fun doRayTracing(setPixelsCallback: (Map<Pair<Int, Int>, Color>) -> Unit) {
//        val currentInstant = Clock.System.now()
        val chunkLength = width / 4
        println("Available processors: ${Runtime.getRuntime().availableProcessors()}")

        val jobs = (0..<width).chunked(chunkLength)
            .parallelStream().forEach { chunk ->
//                println("Processing chunk of size ${chunk.size} at ${Clock.System.now() - currentInstant}")
                val pixels = Array(height) { Array(width) { Color.BLACK } }
                //            async(Dispatchers.Default) {
                var c = Color.BLACK
                for (x in chunk) {
                    for (y in 0..<height) {
                        val level = config.antiAliasMaxLevel
                        for (p in 0..<level) {
                            for (q in 0..<level) {
                                val jitteredX = (x + (p + random()) / level).toFloat()
                                val jitteredY = (y + (q + random()) / level).toFloat()

                                val (u, v) = pixelToUV(
                                    jitteredX,
                                    jitteredY,
                                    ImagePlane(-0.5f, 0.5f, -0.5f, 0.5f)
                                )
                                c += getColorAtPixel(
                                    u, v, camera
                                )
                            }
                        }
                        c = (1f / (level * level)) * c
                        pixels[y][x] = c
                    }
                }
                val m = mutableMapOf<Pair<Int, Int>, Color>()
                for (x in chunk) {
                    for (y in 0..<height) {
                        m[Pair(x, y)] = pixels[y][x]
                    }
                }
                setPixelsCallback(m)
//                println("Completed chunk of size ${chunk.size} in time  ${Clock.System.now() - currentInstant}.")
                //            }
            }
        //        jobs.awaitAll()

//        val duration = Clock.System.now() - currentInstant
//        println("Done drawing pixels. Took: $duration")


    }

    private tailrec fun colorOfRay(
        ray: Ray,
        accumulatedColor: Color = Color.BLACK,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY),
        recursionDepth: Int = 0,
    ): Color {
        if (recursionDepth >= config.maxRecursionDepth) {
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
        val lightSources = scene.getLightSources()


        lightSources.forEach { lightSource ->
            val lightPos = lightSource.position
            val lightDirectionVector = lightPos.toVector3D() - hitPoint.toVector3D()
            val lightDir = lightDirectionVector.normalize()

            val rayFromHitToLight = Ray(hitPoint, lightDir)

            // Compute if the ray from the hit point to the light source intersects with any object
            val shadowIntersection =
                scene.hit(rayFromHitToLight, Interval(0.00001f, Float.POSITIVE_INFINITY))

            color = color + if (shadowIntersection == null) {
                val distanceToLight = lightDirectionVector.normSquared()
                // Scale intensity with inverse square law
                val I = (lightSource.intensity / (4 * PI * distanceToLight)).toFloat()
                val kd = materialColor
                val lambertian = max(0f, normal dot lightDir) * kd

                val vv = -1f * ray.direction
                val h = (vv + lightDir).normalize()
                val ks = material.specularCoefficient
                val phong =
                    max(0.0, (normal dot h).toDouble()).pow(material.phongCoefficient).toFloat() * ks

                I * (lambertian + phong)
            } else {
                Color.BLACK
            }
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
        val width = imagePlane.width()
        val height = imagePlane.height()
        val u = imagePlane.left + width * (i + 0.5f) / this.width
        val v = imagePlane.bottom + height * (j + 0.5f) / this.height
        return Pair(u, v)
    }

    private fun getColorAtPixel(
        u: Float,
        v: Float,
        camera: Camera,
    ): Color {
        val ray = camera.ray(Pair(u, v))
        val color = colorOfRay(
            ray = ray
        )
        return color
    }
}