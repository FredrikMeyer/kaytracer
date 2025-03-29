package net.fredrikmeyer

import kotlin.math.*

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

    var angle = 0.0
    while (true) {
        val lightPos = Point3D(cos(angle).toFloat(), sin(angle).toFloat(), 2f)
        doRayTracing(bbs, width, height, sphere, lightPos)
        viewer.refresh()
        Thread.sleep(100)
        angle += 0.11
    }


    // Keep the program running until the window is closed
    println("Close the viewer window to exit the program")
    Thread.sleep(Long.MAX_VALUE)
}

private fun doRayTracing(
    bbs: BasicBitmapStorage,
    width: Int,
    height: Int,
    sphere: Sphere,
    lightPos: Point3D
) {
    with(bbs) {
        fill(Color(1f, 1f, 1f).toJavaAwt())

        // Draw a red rectangle
        for (x in 0..<width) {
            for (y in 0..<height) {
                val (u, v) = pixelToUV(
                    x,
                    y,
                    -0.5f,
                    0.5f,
                    -0.5f,
                    0.5f,
                    width,
                    height
                )

                val uu = Vector3D(0f, 1f, 0f)
                val vv = Vector3D(0f, 0f, 1f)

                val d = 1f // distance to image plane
                val e = Vector3D(3f, 0f, 0f)
                val ray = Ray(
                    origin = e.toPoint3D(),
                    direction = (-d * Vector3D(-1f, 0f, 0f) + (u * uu + v * vv)).normalize()
                )

                intersectRaySphere(ray, sphere)?.let {
                    val p = ray.pointOnRay(it)
                    val normal = sphere.normalAtPoint(p)
                    val lightDir = (p.toVector3D() - lightPos.toVector3D()).normalize()

                    val I = 2f
                    val kd = 1.5f
                    val lambertian = kd * max(0f, normal dot lightDir)

                    val h = (ray.direction + lightDir).normalize()
                    val ks = 1.0f
                    val phong = ks * max(0f, normal dot h).pow(100.0f)
                    val L = I * (lambertian + phong)
                    val c = Color(min(L, 1f), 0f, 0f)
                    setPixel(x, y, c.toJavaAwt())
                }

            }
        }
    }
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