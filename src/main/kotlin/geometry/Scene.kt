package net.fredrikmeyer.geometry

import net.fredrikmeyer.*

data class Hit(
    val distance: Float,
    val point: Point3D,
    val surface: Surface
)

data class LightSource(
    val position: Point3D,
    val intensity: Float,
    // Ikke i bruk??
    val color: Color = Color.WHITE
)

class Scene(
    private val objects: List<Surface>,
    val ambientLightIntensity: Float = 0.5f,
    private var lightSources: List<LightSource> = listOf(
        LightSource(
            position = Point3D(0f, 0f, 0f),
            intensity = 2.0f
        )
    )
) {

    fun updateLightPosition(lightPosition: Point3D) {
        this.lightSources = listOf(lightSources.first().copy(position = lightPosition))
    }

    fun getLightSources(): List<LightSource> {
        return lightSources
    }

    fun hit(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY)
    ): Hit? {
        val closestObject =
            objects
                .mapNotNull { obj ->
                    obj.geometry.intersect(ray, interval)?.let {
                        // To avoid far away artifacts
                        if (it > 100f) return@mapNotNull null
                        Pair(obj, it)
                    }
                }
                .minByOrNull { it.second }

        if (closestObject == null) {
            return null
        }

        val (obj, distance) = closestObject

        val point = ray.pointOnRay(distance)

        return Hit(
            distance,
            point,
            obj
        )
    }

    fun numberOfSurfaces(): Int {
        return objects.size
    }

    override fun toString(): String {
        return "Scene(ambientLightIntensity=$ambientLightIntensity, objects=$objects, lightSources=$lightSources)"
    }
}

@DslMarker
annotation class RayTracerDsl

fun scene(init: SceneBuilder.() -> Unit): Scene {
    val builder = SceneBuilder()
    builder.init()
    return builder.build()
}

@RayTracerDsl
class SceneBuilder {
    private val surfaces = mutableListOf<Surface>()
    private val lightSources = mutableListOf<LightSource>()
    var ambientLightIntensity: Float = 0.5f

    fun surface(init: SurfaceBuilder.() -> Unit) {
        val builder = SurfaceBuilder()
        builder.init()
        surfaces.add(builder.toSurface())
    }

    fun lightSource(init: LightSourceBuilder.() -> Unit) {
        val lightSourceBuilder = LightSourceBuilder()
        lightSourceBuilder.init()
        lightSources.add(lightSourceBuilder.build())
    }

    operator fun Surface.unaryPlus() {
        surfaces.add(this)
    }

    fun build(): Scene {
        return if (lightSources.isEmpty()) {
            Scene(surfaces, ambientLightIntensity)
        } else {
            Scene(surfaces, ambientLightIntensity, lightSources)
        }
    }
}

@RayTracerDsl
class LightSourceBuilder {
    var position: Point3D = Point3D(0f, 0f, 0f)
    var intensity: Float = 2.0f

    fun position(x: Float, y: Float, z: Float) {
        position = Point3D(x, y, z)
    }

    fun build(): LightSource {
        return LightSource(position, intensity)
    }
}


@RayTracerDsl
class SurfaceBuilder {
    var geometry: GeometricObject? = null
    var material: Material? = null

    fun geometry(geometry: GeometricObject, init: SurfaceBuilder.() -> Unit) {
        this.geometry = geometry
        init()
    }

    fun material(init: MaterialBuilder.() -> Unit): Material {
        val builder = MaterialBuilder()
        builder.init()
        this.material = builder.build()
        return builder.build()
    }

    fun sphere(init: SphereBuilder.() -> Unit) {
        val builder = SphereBuilder()
        builder.init()
        geometry = builder.build()
    }

    fun plane(init: PlaneBuilder.() -> Unit) {
        val builder = PlaneBuilder()
        builder.init()
        geometry = builder.build()
    }

    fun triangle(init: TriangleBuilder.() -> Unit) {
        val builder = TriangleBuilder()
        builder.init()
        geometry = builder.build()
    }

    fun cube(init: CubeBuilder.() -> Unit) {
        val builder = CubeBuilder()
        builder.init()
        geometry = builder.build()
    }

    fun toSurface(): Surface {
        require(geometry != null && material != null) { "Geometry: $geometry, Material: $material" }

        return Surface(
            this@SurfaceBuilder.geometry!!,
            this@SurfaceBuilder.material!!
        )
    }
}

@RayTracerDsl
class CubeBuilder {
    var p1: Point3D = Point3D(0f, 0f, 0f)
    var p2: Point3D = Point3D(1f, 1f, 1f)

    fun build(): Cube {
        return Cube(p1, p2)
    }
}

@RayTracerDsl
class PlaneBuilder {
    var point: Point3D = Point3D(0f, 0f, 0f)
    var normal: Vector3D = Vector3D(0f, 1f, 0f)

    fun build(): Plane {
        return Plane(point, normal)
    }
}


@RayTracerDsl
class TriangleBuilder {
    var p1: Point3D = Point3D(0f, 0f, 0f)
    var p2: Point3D = Point3D(0f, 0f, 0f)
    var p3: Point3D = Point3D(0f, 0f, 0f)

    fun build(): Triangle {
        return Triangle(p1, p2, p3)
    }
}

@RayTracerDsl
class SphereBuilder {
    var center: Point3D = Point3D(0f, 0f, 0f)
    var radius: Float = 1f

    fun center(x: Float, y: Float, z: Float) {
        center = Point3D(x, y, z)
    }

    fun build(): Sphere {
        return Sphere(center, radius)
    }
}

@RayTracerDsl
class MaterialBuilder {
    var color: Color = Color.WHITE
    var reflectivity: Float = 0.3f
    var specularCoefficient: Color = Color.GRAY_LIGHT
    var phongCoefficient: Double = 100.0

    fun build(): Material {
        return Material(color, specularCoefficient, phongCoefficient, reflectivity)
    }
}