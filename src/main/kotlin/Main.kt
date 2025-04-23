
package net.fredrikmeyer

import net.fredrikmeyer.geometry.*
import net.fredrikmeyer.gui.BitmapViewer
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin


// Variable to store the current camera Z position
var currentCameraZ = 3.0f

val scenes = mapOf(
    "1" to scene {
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
            geometry = Triangle(
                a = Point3D(0.5f, 0.2f, 0f),
                b = Point3D(1f, 0.2f, 0f),
                c = Point3D(0f, 1f, 0.3f)
            )
            material {
                color = Color.RED
                reflectivity = 0.1f
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
        // Bottom plane
        surface {
            sphere {
                center = Point3D(-0.1f, -0.9f, 0.5f)
                radius = 0.1f
            }
            material {
                color = Color.MAGENTA
                reflectivity = 0.85f
            }
        }
        surface {
            geometry = Plane(point = Point3D(0.0f, -1f, 0f), normal = Vector3D(0f, 1f, 0.1f))
            material {
                color = Color.GREEN
                reflectivity = 0.7f
            }
        }
    },
    "2" to scene {
        ambientLightIntensity = 0.2f
        surface {
            plane {
                point = Point3D(0.0f, -1f, 0.0f)
                normal = Vector3D(0f, 1f, 0.0f)
            }
            material {
                color = Color.WHITE
                reflectivity = 0.0f
            }
        }
//        surface {
//            triangle {
//                p1 = Point3D(0.0f, 1.0f, 0.0f)
//                p2 = Point3D(0.0f, 0.0f, 0.0f)
//                p3 = Point3D(1.0f, 0.0f, 0.0f)
//            }
//            material {
//                color = Color.RED
//                reflectivity = 0.0f
//            }
//        }
//        surface {
//            triangle {
//                p1 = Point3D(0.0f, 1.0f, 0.0f)
//                p2 = Point3D(1.0f, 0.0f, 0.0f)
//                p3 = Point3D(1.0f, 1.0f, 0f)
//            }
//            material {
//                color = Color.BLUE
//            }
//        }
        surface {
            geometry = Cube(Point3D(0.0f, 0.0f, -1.0f), Point3D(1.0f, 1.0f, 0.0f))
            material {
                color = Color.BLUE
            }
        }
    })

fun main() {
    val scene = scenes["2"]!!


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

    val rayTracer = RayTracer(
        width = width,
        height = height,
        scene = scene,
        antiAliasMaxLevel = 2,
        maxRecursionDepth = 3
    )

    while (viewer.isVisible()) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFrameTime

        if (elapsedTime >= targetFrameTime) {
            val lightPos = Point3D(1.5f * cos(angle).toFloat(), 1.5f, 1.5f * sin(angle).toFloat())
            scene.lightPosition = lightPos
            val colors = rayTracer.doRayTracing()
            bbs.setPixels(colors)
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

fun staticMain(): BufferedImage {
    val scene = scenes["1"]!!
    val rayTracer = RayTracer(
        width = 700,
        height = 700,
        scene = scene,
        antiAliasMaxLevel = 2,
        maxRecursionDepth = 3
    )
    val bbs = BasicBitmapStorage(700, 700)
    val colors = rayTracer.doRayTracing()
    bbs.setPixels(colors)
    return bbs.image
}

data class ImagePlane(
    val left: Float,
    val right: Float,
    val bottom: Float,
    val top: Float,
)


