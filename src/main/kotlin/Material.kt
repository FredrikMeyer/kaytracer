package net.fredrikmeyer

data class Material(
    val color: Color,
    val specularCoefficient: Color = Color.GRAY_LIGHT,
    val phongCoefficient: Double = 100.0,
    val reflectivity: Float = 0.3f
)

/*
// Different types of interactions a ray can have with a material
sealed class MaterialInteraction {
    data class Reflection(val ray: Ray, val strength: Float) : MaterialInteraction()

    //    data class Refraction(val ray: Ray, val strength: Float) : MaterialInteraction()
    data class Absorption(val color: Color, val strength: Float) : MaterialInteraction()
//    data class Emission(val color: Color, val strength: Float) : MaterialInteraction()
}


// A material component represents a specific behavior
interface MaterialComponent {
    fun process(ray: Ray, hit: Hit): MaterialInteraction?
}


interface Material2 {
    fun interact(ray: Ray,hit: Hit): List<MaterialInteraction>
}

// Component for diffuse reflection
class DiffuseComponent(val color: Color, val strength: Float = 1.0f) : MaterialComponent {
    override fun process(ray: Ray, hit: Hit): MaterialInteraction {
        // Calculate diffuse reflection
        return MaterialInteraction.Absorption(color, strength)
    }
}

// Component for specular reflection
class SpecularComponent(
    val coefficient: Color,
    val phongExponent: Double,
    val strength: Float = 1.0f
) : MaterialComponent {
    override fun process(ray: Ray, hit: Hit): MaterialInteraction {
        // Calculate specular reflection
        // ...
//        return MaterialInteraction.Reflection(reflectedRay, strength)
        TODO()
    }
}


// A material made up of multiple components
class CompositeMaterial(private val components: List<MaterialComponent>) : Material2 {
    override fun interact(
        ray: Ray,
        hit: Hit
    ): List<MaterialInteraction> {
        return components.mapNotNull { it.process(ray, hit) }
    }
}


object Materials {
    fun diffuse(color: Color): Material2 {
        return CompositeMaterial(
            listOf(
                DiffuseComponent(color, 1.0f)
            )
        )
    }

    fun metal(color: Color, reflectivity: Float, fuzziness: Float = 0.0f): Material2 {
        return CompositeMaterial(
            listOf(
                DiffuseComponent(color, 1.0f - reflectivity),
                SpecularComponent(Color.GRAY_LIGHT, 100.0, reflectivity)
            )
        )
    }


}
 */