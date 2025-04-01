package net.fredrikmeyer.geometry

import net.fredrikmeyer.*

data class Hit(
    val distance: Float,
    val point: Point3D,
    val surface: Surface
)

class Scene(private val objects: List<Surface>) {
    fun hit(
        ray: Ray,
        interval: Interval = Interval(0f, Float.POSITIVE_INFINITY)
    ): Hit? {
        val closestObject =
            objects
                .mapNotNull { obj -> obj.geometry.intersect(ray, interval)?.let { Pair(obj, it) } }
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

    fun surface(init: SurfaceBuilder.() -> Unit) {
        val builder = SurfaceBuilder()
        builder.init()
        surfaces.add(builder.toSurface()!!)
    }

    operator fun Surface.unaryPlus() {
        surfaces.add(this)
    }

    fun build(): Scene {
        return Scene(surfaces)
    }
}


@RayTracerDsl
class SurfaceBuilder {
    var geometry: GeometricObject? = null
    var material: Material? = null

    fun sphere(center: Point3D, radius: Float = 1f) {
        geometry = Sphere(center, radius)
    }

    fun geometry(geometry: GeometricObject, init: SurfaceBuilder.() -> Unit) {
        this.geometry = geometry
        init()
    }

    fun material(init: MaterialBuilder.() -> Unit): Material {
        val builder = MaterialBuilder()
        builder.init()
        return builder.build()
    }

    fun toSurface(): Surface? {
        if (geometry == null || material == null) return null

        return object : Surface {
            override val geometry: GeometricObject = this@SurfaceBuilder.geometry!!
            override val material: Material = this@SurfaceBuilder.material!!
        }
    }
}

@RayTracerDsl
class MaterialBuilder {
    var color: Color = Color.WHITE

    fun build(): Material {
        return Material(color)
    }
}
