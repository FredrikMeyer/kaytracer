package net.fredrikmeyer

import net.fredrikmeyer.geometry.*
import net.fredrikmeyer.gui.BitmapViewer
import kotlin.math.cos
import kotlin.math.sin


val config = RayTracerConfig(
    width = 700,
    height = 700,
    antiAliasMaxLevel = 2,
    maxRecursionDepth = 30
)

data class State(
    var currentCameraZ: Float = 3.0f,
    var rotationAngle: Double = Math.PI / 2.0,
    var paused: Boolean = true,
    var needsRerender: Boolean = true
)

@Suppress("unused")
object Scenes {
    val simple = scene {
        ambientLightIntensity = 0.1f
        lightSource {
            position = Point3D(2.5f, 2f, 0f)
            intensity = 100f
        }
        surface {
            sphere {
                radius = 1f
                center = Point3D(0.5f, 0f, 0f)
            }
            material {
                color = Color.RED
                reflectivity = 0.0f
            }
        }
    }
    val spheres = scene {
        ambientLightIntensity = 0.5f
        lightSource {
            position = Point3D(-0.5f, 3.5f, 0.5f)
            intensity = 100f
        }
        lightSource {
            position = Point3D(-1.5f, 3.5f, 0.5f)
            intensity = 100f
        }
        surface {
            sphere {
                radius = 1f
                center = Point3D(0.5f, 0f, 0f)
            }
            material {
                color = Color.RED
                reflectivity = 0.2f
                phongCoefficient = 1000.0
                specularCoefficient = Color.WHITE
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
                specularCoefficient = Color.WHITE
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
        // Bottom plane
        surface {
            plane {
                point = Point3D(0.0f, -1f, 0f)
                normal = Vector3D(0f, 1f, 0.0f)
            }
            material {
                color = Color.GREEN
                reflectivity = 0.5f
                phongCoefficient = 100.0
            }
        }
    }
    val cube = scene {
        ambientLightIntensity = 0.2f
        lightSource {
            position(0f, 3.0f, 0f)
            intensity = 1000f
        }
        lightSource {
            position(0f, -3.0f, 0f)
            intensity = 1000f
        }
        lightSource {
            position(1.5f, 0.5f, 0.5f)
            intensity = 1000f
        }
        surface {
            sphere { radius = 0.1f; center = Point3D(0.0f, 0.0f, 0.0f) }
            material {
                color = Color.RED
                reflectivity = 0.5f
                phongCoefficient = 100.0
                specularCoefficient = Color.WHITE
            }
        }
        surface {
            geometry = Cube(Point3D(0.0f, -1f, -1.0f), Point3D(1.0f, 0.0f, 0.0f)).translate(
                Vector3D(
                    0.0f,
                    0.0f,
                    -0.5f
                )
            )
            material {
                color = Color.GREEN
                reflectivity = 0.5f
                phongCoefficient = 20.0
            }
        }
        surface {
            geometry = Cube(Point3D(0.0f, -1f, -1.0f), Point3D(1.0f, 0.0f, 0.0f)).translate(
                Vector3D(
                    -2.0f,
                    1.0f,
                    0.0f
                )
            )
            material {
                color = Color.BLUE
                reflectivity = 0.3f
            }
        }
    }


    val cornellBox = scene {
        ambientLightIntensity = 0.1f
        lightSource {
            position = Point3D(0f, 0.0f, 3f)
            intensity = 100f
        }
        surface {
            val r1 =  0.45f
            sphere {
                radius = r1
                center = Point3D(-0.3f, -1 + r1, 0.2f)
            }
            material {
                color = Color.RED
                reflectivity = 0.5f
                phongCoefficient = 100.0
                specularCoefficient = Color.WHITE
            }
        }
        val r = 0.1f
        surface {
            sphere {
                radius = r
                center = Point3D(0.5f, -1 + r, 0.5f)
            }
            material {
                color = Color.BLUE
                reflectivity = 0.5f
                specularCoefficient = Color.WHITE
            }
        }
        val r2 = 0.15f
        surface {
            sphere {
                radius = r2
                center = Point3D(0.2f, -1f + r2, 0.7f)
            }
            material {
                color = Color.YELLOW
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, -1f, -1f)
                p3 = Point3D(1f, -1f, -1f)
                p2 = Point3D(-1f, 1f, -1f)
            }
            material {
                color = Color.WHITE
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(1f, -1f, -1f)
                p3 = Point3D(1f, 1f, -1f)
                p2 = Point3D(-1f, 1f, -1f)
            }
            material {
                color = Color.WHITE
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, -1f, -1f)
                p2 = Point3D(-1f, 1f, -1f)
                p3 = Point3D(-1f, -1f, 1f)
            }
            material {
                color = Color.GREEN
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, 1f, -1f)
                p2 = Point3D(-1f, 1f, 1f)
                p3 = Point3D(-1f, -1f, 1f)
            }
            material {
                color = Color.GREEN
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(1f, -1f, -1f)
                p2 = Point3D(1f, -1f, 1f)
                p3 = Point3D(1f, 1f, -1f)
            }
            material {
                color = Color.RED
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(1f, 1f, -1f)
                p2 = Point3D(1f, -1f, 1f)
                p3 = Point3D(1f, 1f, 1f)
            }
            material {
                color = Color.RED
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, -1f, -1f)
                p2 = Point3D(1f, -1f, -1f)
                p3 = Point3D(1f, -1f, 1f)
            }
            material {
                color = Color.GRAY_LIGHT
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, -1f, -1f)
                p2 = Point3D(1f, -1f, 1f)
                p3 = Point3D(-1f, -1f, 1f)
            }
            material {
                color = Color.GRAY_LIGHT
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, 1f, -1f)
                p2 = Point3D(1f, 1f, -1f)
                p3 = Point3D(1f, 1f, 1f)
            }
            material {
                color = Color.GRAY_LIGHT
                reflectivity = 0.1f
            }
        }
        surface {
            triangle {
                p1 = Point3D(-1f, 1f, -1f)
                p2 = Point3D(1f, 1f, 1f)
                p3 = Point3D(-1f, 1f, 1f)
            }
            material {
                color = Color.GRAY_LIGHT
                reflectivity = 0.1f
            }
        }
    }
}

fun main() {
    println("Available processors: ${Runtime.getRuntime().availableProcessors()}")
    val scene = Scenes.cornellBox
    println(scene)

    // Create a bitmap with a simple pattern
    val bbs = BasicBitmapStorage(config.width, config.height)

    val state = State()
    // Display the bitmap using our new BitmapViewer class with camera position slider
    val viewer = BitmapViewer(
        bitmapStorage = bbs,
        title = "Ray Tracer",
        minCameraZ = 1.0f,
        maxCameraZ = 10.0f,
        state = state,
        cameraPositionChangeListener = { newCameraZ ->
            state.currentCameraZ = newCameraZ
            if (state.paused) {
                state.needsRerender = true
            }
            this.refresh()
        },
        rotationChangeListener = { newRotationAngle ->
            state.rotationAngle = newRotationAngle
            if (state.paused) {
                state.needsRerender = true
            }
            this.refresh()
        }
    )

    viewer.show()

    // Poll until the frame becomes visible
    while (!viewer.isVisible()) {
        // Short sleep to avoid busy-waiting
        Thread.sleep(10)
    }

    var lastFrameTime = System.currentTimeMillis()

    val camera = Camera(
        seeFrom = Point3D(0.5f, 0f, 10f),
        lookAt = Point3D(0, 0, 0)
    )

    val rayTracer = RayTracer(
        scene = scene,
        config = config,
        camera = camera,
    )

    val targetFrameTime = 100 // 100ms between frames (10 FPS)
    while (viewer.isVisible()) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFrameTime

        val angle = state.rotationAngle

        if (elapsedTime >= targetFrameTime) {
//            val lightPos = Point3D(1.5f * cos(angle).toFloat(), 3f, 1.5f * sin(angle).toFloat())
//            scene.updateLightPosition(lightPos)
            camera.seeFrom = Point3D(
                x = state.currentCameraZ * cos(angle),
                z = state.currentCameraZ * sin(angle),
                y = 0.0
            )
            if (!state.paused || state.needsRerender) {
                rayTracer.doRayTracing {
                    bbs.setPixels(it)
                    viewer.refresh()
                }
                if (!state.paused) {
                    state.rotationAngle += 0.05
                }
                state.needsRerender = false
                println(state)
                println(camera)
            }
            viewer.refresh()
            lastFrameTime = currentTime
        } else {
            // Short sleep to avoid busy-waiting
            Thread.sleep(10)
        }
    }

    // The loop has exited, which means the window was closed
    println("Window closed, exiting program")
    viewer.close()
}
