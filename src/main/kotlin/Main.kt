package net.fredrikmeyer

import net.fredrikmeyer.geometry.*
import net.fredrikmeyer.gui.BitmapViewer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

fun main() {
    println("Hello World!")

    val sphere = Sphere(Point3D(0f, 0f, 0f), 1f)

    // Create a bitmap with a simple pattern
    val width = 600
    val height = 600
    val bbs = BasicBitmapStorage(width, height)

    // Display the bitmap using our new BitmapViewer class
    val viewer = BitmapViewer(bbs, "Sample Image")
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
            val lightPos = Point3D(2f, cos(angle).toFloat(), sin(angle).toFloat())
            doRayTracing(bbs, width, height, sphere, lightPos)
            viewer.refresh()
            lastFrameTime = currentTime
            angle += 0.11
        } else {
            // Short sleep to avoid busy-waiting
            Thread.sleep(1)
        }
    }

    // The loop has exited, which means the window was closed
    println("Window closed, exiting program")
    viewer.close()
}

private fun doRayTracing(
    bbs: BasicBitmapStorage,
    width: Int,
    height: Int,
    sphere: Sphere,
    lightPos: Point3D
) {
    bbs.fill(Color(1f, 1f, 1f).toJavaAwt())

    // Draw a red rectangle
    for (x in 0..<width) {
        for (y in 0..<height) {
            val color = colorOfPixel(
                x = x,
                y = y,
                width = width,
                height = height,
                l = -0.5f,
                r = 0.5f,
                b = -0.5f,
                t = 0.5f,
                geometricObject = sphere,
                lightPos = lightPos
            )
            bbs.setPixel(x, y, color.toJavaAwt())
        }
    }
}

fun colorOfPixel(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    l: Float,
    r: Float,
    b: Float,
    t: Float,
    geometricObject: GeometricObject,
    lightPos: Point3D
): Color {
    val (u, v) = pixelToUV(
        x,
        y,
        l, r, b, t,
        width,
        height
    )

    val uu = Vector3D(1f, 0f, 0f)
    val vv = Vector3D(0f, 1f, 0f)
    val w = Vector3D(0f, 0f, 1f)

    val e = Point3D(0f, 0f, 3f)

    val d = 1f
    val ray = Ray(
        origin = e,
        direction = (-d * w + (u * uu + v * vv)).normalize()
    )

    return geometricObject.intersect(ray)?.let {
        val normal = geometricObject.normalAtPoint(it)
        val lightDir = (it.toVector3D() - lightPos.toVector3D()).normalize()

        val I = 1.0f
        val kd = Color.RED
        val lambertian = max(0f, normal dot lightDir) * kd

        val vv = -1f * ray.direction
        val h = (vv + lightDir).normalize()
        val ks = Color.WHITE
        val phong = max(0.0, (normal dot h).toDouble()).pow(10.0).toFloat() * ks

        // Ambient light
        val ka = Color.RED
        val Iambient = 0.4f
        val ambientIntensity = Iambient * ka;

        val L = I * (lambertian + phong) + ambientIntensity
        val c = L
        c
    } ?: Color(1f, 1f, 1f)
}


fun pixelToUV(
    i: Int,
    j: Int,
    l: Float,
    r: Float,
    b: Float,
    t: Float,
    pixelWidth: Int,
    pixelHEight: Int
): Pair<Float, Float> {
    val width = r - l
    val height = t - b
    val u = l + width * (i + 0.5f) / pixelWidth;
    val v = b + height * (j + 0.5f) / pixelHEight;
    return Pair(u, v)
}
