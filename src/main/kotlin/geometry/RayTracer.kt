package net.fredrikmeyer.geometry

import net.fredrikmeyer.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.time.Clock

import kotlin.time.ExperimentalTime

data class RayTracerConfig(
    val width: Int = 700,
    val height: Int = 700,
    val maxRecursionDepth: Int = 3,
    val antiAliasMaxLevel: Int = 3,
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
        val currentInstant = Clock.System.now()
        val chunkLength = width / Runtime.getRuntime().availableProcessors()


        (0..<width).chunked(chunkLength)
            .parallelStream().forEach { chunk ->
//                println("Processing chunk of size ${chunk.size} at ${Clock.System.now() - currentInstant}")
                val pixels = Array(height) { Array(width) { Color.BLACK } }

                val r = ThreadLocalRandom.current()
                for (x in chunk) {
                    for (y in 0..<height) {
                        var c = Color.BLACK
                        val level = config.antiAliasMaxLevel
                        for (p in 0..<level) {
                            for (q in 0..<level) {
                                val xJitter =
                                    if (p > 0) ((p + r.nextDouble()) / level) else p.toDouble()
                                val yJitter =
                                    if (q > 0) (q + r.nextDouble()) / level else q.toDouble()

                                val jitteredX = (x + xJitter).toFloat()
                                val jitteredY = (y + yJitter).toFloat()

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
            }

        val duration = Clock.System.now() - currentInstant
        println("Done drawing pixels. Took: $duration")

    }

    private fun colorOfRay(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY),
        recursionDepth: Int = 0,
    ): Color {
        val hit = scene.hit(ray, interval = interval)
        if (hit == null) {
            return Color.BLACK
        }

        val hitPoint = hit.point
        val material = hit.surface.material

        // TODO: vil ikke ambient light bli lagt til altfor mange ganger her?
        // Ambient light
        // k_a
        var color = Color.BLACK
        val materialColor = material.color
        color = if (recursionDepth == 0) scene.ambientLightIntensity * materialColor else {
            color
        }

        val normal = hit.surface.geometry.normalAtPoint(hitPoint)
        val lightSources = scene.getLightSources()

        lightSources.forEach { lightSource ->
            val lightPos = lightSource.position
            val lightDirectionVector = lightPos.toVector3D() - hitPoint.toVector3D()
            val lightDir = lightDirectionVector

            val distanceToLight = lightDirectionVector.normSquared()
            val rayFromHitToLight = Ray(hitPoint, lightDir)

            // Compute if the ray from the hit point to the light source intersects with any object
            color = color + if (!isInShadow(rayFromHitToLight, distanceToLight)) {
                // Scale intensity with inverse square law
                val I = (lightSource.intensity / (4 * PI * distanceToLight)).toFloat()
                val kd = materialColor
                val normalizedLightDir = lightDir.normalize()
                val lambertian = max(0f, normal dot normalizedLightDir) * kd

                val vv = -1f * ray.direction
                val h = (vv + normalizedLightDir).normalize()
                val ks = material.specularCoefficient
                val phong =
                    max(0.0, (normal dot h).toDouble()).pow(material.phongCoefficient)
                        .toFloat() * ks

                I * (lambertian + phong)
            } else {
                Color.BLACK
            }
        }

        val reflectionVector = createReflectionVector(ray, normal)
        val newRay = Ray(hitPoint, reflectionVector)
        val reflectivity = material.reflectivity

        val directContribution = (1 - reflectivity) * color
        return if (reflectivity > 0 || recursionDepth < config.maxRecursionDepth) {
            val acc = directContribution +
                    (reflectivity * colorOfRay(
                        newRay,
                        interval = Interval(0.0001f, Float.POSITIVE_INFINITY),
                        recursionDepth + 1
                    ))
            acc
        } else {
            directContribution
        }
    }

    private fun isInShadow(ray: Ray, distanceToLight: Float): Boolean {
        // TODO: doesn't need a Hit object, only true/false
        return scene.hit(ray, Interval(0.0001f, distanceToLight)) != null
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