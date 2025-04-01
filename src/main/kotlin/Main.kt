package net.fredrikmeyer

import net.fredrikmeyer.geometry.*
import net.fredrikmeyer.gui.BitmapViewer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

// Variable to store the current camera Z position
var currentCameraZ = 3.0f

const val MAX_RECURSION_DEPTH = 5

fun main() {
    println("Hello World!")

    val sphere1 =
        SphericalSurface(Sphere(Point3D(0.5f, 0f, 0f), 1f), material = Material(color = Color.RED))
    val sphere2 = SphericalSurface(
        Sphere(Point3D(-1f, -0.5f, 0f), 0.5f),
        material = Material(color = Color.BLUE)
    )
    val sphere3 = SphericalSurface(
        geometry = Sphere(Point3D(-0.5f, -0.8f, 0.5f), 0.2f),
        material = Material(color = Color.YELLOW)
    )
    val plane = PlanarSurface(
        Plane(point = Point3D(0f, -1f, 0f), normal = Vector3D(0f, 1f, 0f)),
        material = Material(color = Color.GREEN)
    )

    val scene = Scene(listOf(sphere1, sphere2, plane, sphere3))

    // Create a bitmap with a simple pattern
    val width = 600
    val height = 600
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

    var angle = 0.0
    var lastFrameTime = System.currentTimeMillis()
    val targetFrameTime = 40 // 100ms between frames (10 FPS)

    while (viewer.isVisible()) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFrameTime

        if (elapsedTime >= targetFrameTime) {
            val lightPos = Point3D(1.5f * cos(angle).toFloat(), 1.5f, 1.5f * sin(angle).toFloat())
            doRayTracing(bbs, width, height, scene, lightPos)
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
    val l: Float,
    val r: Float,
    val b: Float,
    val t: Float,
)

private fun doRayTracing(
    bbs: BasicBitmapStorage,
    width: Int,
    height: Int,
    scene: Scene,
    lightPos: Point3D
) {
    // Draw a red rectangle
    for (x in 0..<width) {
        for (y in 0..<height) {
            val uvCoordinates = pixelToUV(
                x,
                y,
                ImagePlane(-0.5f, 0.5f, -0.5f, 0.5f),
                width,
                height
            )
            val ray = rayAtPoint(uvCoordinates.first, uvCoordinates.second)
            val color = colorOfPixel(
                scene = scene,
                lightPos = lightPos,
                ray = ray
            )
            bbs.setPixel(x, y, color.toJavaAwt())
        }
    }
}

fun colorOfPixel(
    scene: Scene,
    lightPos: Point3D,
    ray: Ray,
    interval: Interval = Interval(0f, Float.POSITIVE_INFINITY),
    recursionDepth: Int = 0,
): Color {
    if (recursionDepth >= MAX_RECURSION_DEPTH) {
        return Color.BLACK
    }

    return scene.hit(ray, interval = interval)?.let {
        val point = it.point
        val normal = it.normal
        val lightDir = (lightPos.toVector3D() - point.toVector3D()).normalize()

        // Ambient light
        val ka = it.surface.material.color
        val Iambient = 0.4f
        val ambientIntensity = Iambient * ka;

        val rayFromHit = Ray(point, lightDir)
        val res = scene.hit(rayFromHit, Interval(0.00001f, Float.POSITIVE_INFINITY))

        val shadowContribution = if (res == null) {
            val I = 1.0f
            val kd = it.surface.material.color
            val lambertian = max(0f, normal dot lightDir) * kd

            val vv = -1f * ray.direction
            val h = (vv + lightDir).normalize()
            val ks = Color.WHITE
            val phong = max(0.0, (normal dot h).toDouble()).pow(50.0).toFloat() * ks

            I * (lambertian + phong)
        } else {
            Color.BLACK
        }
        val r = ray.direction - 2f * (ray.direction dot normal) * normal
        val newRay = Ray(point, r)
        val km = Color(0.1f, 0.1f, 0.1f)
        return ambientIntensity + shadowContribution + km * colorOfPixel(
            scene,
            lightPos,
            newRay,
            interval = Interval(0.00001f, Float.POSITIVE_INFINITY),
            recursionDepth + 1
        )
    } ?: Color.WHITE
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
    i: Int,
    j: Int,
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
