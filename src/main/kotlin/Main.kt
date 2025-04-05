@file:OptIn(ExperimentalTime::class)

package net.fredrikmeyer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fredrikmeyer.geometry.*
import net.fredrikmeyer.gui.BitmapViewer
import java.lang.Math.random
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Variable to store the current camera Z position
var currentCameraZ = 3.0f

const val MAX_RECURSION_DEPTH = 30

fun main() {
    println("Hello World!")
    val union = UnionOfSpheres(Sphere(Point3D(0.0f, 0f, 0f), 1f), Sphere(Point3D(0.5f, 0f, 0f), 1f))

    val scene = scene {
        ambientLightIntensity = 2f
        surface {
            sphere {
                radius = 1f
                center = Point3D(0.5f, 0f, 0f)
            }
            material {
                color = Color.RED
                reflectivity = 0.6f
            }
        }
        surface {
            sphere {
                center = Point3D(-1f, -0.5f, 0f)
                radius = 0.5f
            }
            material {
                color = Color.BLUE
                reflectivity = 0.7f
            }
        }
        surface {
            sphere {
                center = Point3D(-1f, 0.5f, 0f)
                radius = 0.25f
            }
            material {
                color = Color.WHITE
                reflectivity = 0.7f
            }
        }
        surface {
            sphere {
                center = Point3D(-0.5f, -0.8f, 0.5f)
                radius = 0.2f
            }
            material {
                color = Color.YELLOW
                reflectivity = 0.75f
            }
        }
//        +IntersectionOfSpheres(Sphere(
//            center = Point3D(0.0f, 0f, 0f),
//            radius = 1f
//        ), Sphere(
//            center =  Point3D(-0.5f, 0.5f, 0f),
//            radius = 1f
//        ))
//        +sphere3
        surface {
            geometry = Plane(point = Point3D(0.0f, -1f, 0f), normal = Vector3D(0f, 1f, 0.1f))
            material {
                color = Color.GREEN
                reflectivity = 0.7f
            }
        }
    }
    println(scene.numberOfSurfaces())

    // Create a bitmap with a simple pattern
    val width = 700
    val height = 700
    val bbs = BasicBitmapStorage(width, height)

    // Display the bitmap using our new BitmapViewer class with camera position slider
    val viewer = BitmapViewer(
        bitmapStorage = bbs,
        title = "Ray Tracer - Adjust Camera Position",
        initialCameraZ = currentCameraZ,
        minCameraZ = 1.0f,
        maxCameraZ = 10.0f
    )

    // Set up the camera position change listener
    viewer.setCameraPositionChangeListener { newCameraZ ->
        currentCameraZ = newCameraZ
        viewer.refresh()
    }

    viewer.show()

    // Poll until the frame becomes visible
    while (!viewer.isVisible()) {
        // Short sleep to avoid busy-waiting
        Thread.sleep(10)
    }

    var angle = Math.PI / 2
    var lastFrameTime = System.currentTimeMillis()
    val targetFrameTime = 1000 // 100ms between frames (10 FPS)

    while (viewer.isVisible()) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFrameTime

        if (elapsedTime >= targetFrameTime) {
            val lightPos = Point3D(1.5f * cos(angle).toFloat(), 1.5f, 1.5f * sin(angle).toFloat())
            scene.lightPosition = lightPos
            doRayTracing(bbs, width, height, scene)
            viewer.refresh()
            lastFrameTime = currentTime
            angle += 0.05
        } else {
            // Short sleep to avoid busy-waiting
            Thread.sleep(10)
        }
    }

    // The loop has exited, which means the window was closed
    println("Window closed, exiting program")
    viewer.close()
}

data class ImagePlane(
    val left: Float,
    val right: Float,
    val bottom: Float,
    val top: Float,
)


private fun doRayTracing(
    bbs: BasicBitmapStorage,
    width: Int,
    height: Int,
    scene: Scene,
) = runBlocking {
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
                                width,
                                height,
                                scene
                            )
                        }
                    }
                    c = (1f / (level * level)) * c
                    pixels[y][x] =  c
                }
            }
        }
    }
    jobs.joinAll()

    for (y in 0 until height) {
        for (x in 0 until width) {
            bbs.setPixel(x, y, pixels[y][x].toJavaAwt())
        }
    }

    val duration = Clock.System.now() - currentInstant
    println("Done drawing pixels. Took: $duration")
}

private fun getColorAtPixel(
    x: Float,
    y: Float,
    width: Int,
    height: Int,
    scene: Scene
): Color {
    val uvCoordinates = pixelToUV(
        x,
        y,
        ImagePlane(-0.5f, 0.5f, -0.5f, 0.5f),
        width,
        height
    )
    val ray = rayAtPoint(uvCoordinates.first, uvCoordinates.second)
    val color = colorOfRay(
        scene = scene,
        ray = ray
    )
    return color
}

fun colorOfRay(
    scene: Scene,
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
                scene,
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


fun pixelToUV(
    i: Float,
    j: Float,
    imagePlane: ImagePlane,
    pixelWidth: Int,
    pixelHEight: Int
): Pair<Float, Float> {
    val (l, r, b, t) = imagePlane
    val width = r - l
    val height = t - b
    val u = l + width * (i + 0.5f) / pixelWidth;
    val v = b + height * (j + 0.5f) / pixelHEight;
    return Pair(u, v)
}
