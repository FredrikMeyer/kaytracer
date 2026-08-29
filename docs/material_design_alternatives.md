# Material Design Alternatives for KayTracer

This document outlines different approaches for implementing materials in the KayTracer ray tracing system, with a focus on interfaces, sealed interfaces, and performance considerations.

## Current Implementation

The current implementation uses a simple data class:

```kotlin
data class Material(
    val color: Color,
    val specularCoefficient: Color = Color.GRAY_LIGHT,
    val phongCoefficient: Double = 100.0,
    val reflectivity: Float = 0.3f
)
```

This approach implements a basic Phong reflection model with:
- Diffuse reflection (using color)
- Specular reflection (using specularCoefficient and phongCoefficient)
- Perfect mirror reflection (using reflectivity)

### Pros
- Simple and easy to understand
- Efficient for basic materials
- Low memory overhead

### Cons
- Limited flexibility for different material types
- Difficult to extend with new material behaviors
- All materials must have the same properties, even if they don't use them
- No support for more complex materials like glass, subsurface scattering, etc.

## Alternative 1: Interface-based Approach

```kotlin
interface Material {
    fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color
}

// Example implementations
class LambertianMaterial(val albedo: Color) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation for diffuse material
    }
}

class MetalMaterial(val albedo: Color, val fuzz: Float = 0.0f) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation for metal material
    }
}

class DielectricMaterial(val refractiveIndex: Float) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation for glass-like material
    }
}
```

> **Note**: A sealed interface could be used instead of a regular interface to gain significant benefits:
> - **Exhaustive pattern matching**: The compiler ensures all possible implementations are handled in `when` expressions
> - **Better compiler optimizations**: The compiler knows all possible implementations at compile time
> - **Enhanced type safety**: Restricts inheritance to a known set of classes
> - **Clear API boundaries**: Implementations are limited to the module where the sealed interface is defined
>
> This approach is detailed in [Alternative 2: Sealed Interface Approach](#alternative-2-sealed-interface-approach) and is particularly valuable for material systems where you want to ensure all material types are properly handled.

### Pros
- Highly flexible and extensible
- Each material type can have its own properties
- Easy to add new material types
- Clear separation of concerns

### Cons
- Potentially less efficient due to virtual method calls
- More complex implementation
- May require more memory for many small objects
- Harder to serialize/deserialize

## Alternative 2: Sealed Interface Approach

```kotlin
sealed interface Material {
    fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color
}

class LambertianMaterial(val albedo: Color) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation
    }
}

class MetalMaterial(val albedo: Color, val fuzz: Float) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation
    }
}

// Other material types...
```

### Pros
- All the benefits of the interface approach
- Exhaustive when used in when expressions
- Compiler can optimize better knowing all possible implementations
- Better type safety

### Cons
- Cannot be extended outside the file/module where it's defined
- Still has virtual method call overhead
- More complex than the data class approach

## Alternative 3: Composition with Material Components

```kotlin
interface MaterialComponent {
    fun process(ray: Ray, hit: Hit, scene: Scene): MaterialInteraction?
}

sealed class MaterialInteraction {
    data class Reflection(val ray: Ray, val strength: Float) : MaterialInteraction()
    data class Refraction(val ray: Ray, val strength: Float) : MaterialInteraction()
    data class Absorption(val color: Color, val strength: Float) : MaterialInteraction()
    data class Emission(val color: Color, val strength: Float) : MaterialInteraction()
}

class CompositeMaterial(private val components: List<MaterialComponent>) : Material {
    override fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        val interactions = components.mapNotNull { it.process(ray, hit, scene) }
        // Process interactions to compute final color
    }
}

// Example components
class DiffuseComponent(val color: Color, val strength: Float = 1.0f) : MaterialComponent {
    override fun process(ray: Ray, hit: Hit, scene: Scene): MaterialInteraction {
        // Calculate diffuse reflection
        return MaterialInteraction.Absorption(color, strength)
    }
}

class SpecularComponent(val coefficient: Color, val phongExponent: Double, val strength: Float = 1.0f) : MaterialComponent {
    override fun process(ray: Ray, hit: Hit, scene: Scene): MaterialInteraction {
        // Calculate specular reflection
        return MaterialInteraction.Reflection(reflectedRay, strength)
    }
}
```

### Pros
- Highly flexible and composable
- Materials can be built from reusable components
- Easy to create complex materials with multiple behaviors
- Clear separation of concerns

### Cons
- More complex implementation
- Potentially higher memory usage
- May have performance overhead from multiple component evaluations
- More difficult to reason about the overall behavior

## Alternative 4: Enum-based Material Types with Properties

```kotlin
enum class MaterialType {
    LAMBERTIAN,
    METAL,
    DIELECTRIC,
    EMISSIVE
}

class Material(
    val type: MaterialType,
    val albedo: Color = Color.WHITE,
    val specularCoefficient: Color = Color.GRAY_LIGHT,
    val phongCoefficient: Double = 100.0,
    val reflectivity: Float = 0.0f,
    val refractiveIndex: Float = 1.0f,
    val emissionStrength: Float = 0.0f
) {
    fun computeColor(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        return when (type) {
            MaterialType.LAMBERTIAN -> computeLambertian(ray, hit, scene)
            MaterialType.METAL -> computeMetal(ray, hit, scene, depth)
            MaterialType.DIELECTRIC -> computeDielectric(ray, hit, scene, depth)
            MaterialType.EMISSIVE -> computeEmissive()
        }
    }
    
    private fun computeLambertian(ray: Ray, hit: Hit, scene: Scene): Color {
        // Implementation
    }
    
    private fun computeMetal(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation
    }
    
    private fun computeDielectric(ray: Ray, hit: Hit, scene: Scene, depth: Int): Color {
        // Implementation
    }
    
    private fun computeEmissive(): Color {
        return emissionStrength * albedo
    }
}
```

### Pros
- Simple to use
- Efficient dispatch using enum
- All properties in one place
- Easy to serialize/deserialize

### Cons
- All materials have all properties, even if unused
- Adding new material types requires modifying the Material class
- Less flexible than interface-based approaches
- May waste memory on unused properties

## Alternative 5: Functional Approach with Higher-Order Functions

```kotlin
typealias MaterialFunction = (Ray, Hit, Scene, Int) -> Color

class Material(val computeColor: MaterialFunction) {
    companion object {
        fun lambertian(albedo: Color): Material {
            return Material { ray, hit, scene, _ ->
                // Implementation for diffuse material
            }
        }
        
        fun metal(albedo: Color, fuzz: Float): Material {
            return Material { ray, hit, scene, depth ->
                // Implementation for metal material
            }
        }
        
        fun dielectric(refractiveIndex: Float): Material {
            return Material { ray, hit, scene, depth ->
                // Implementation for glass-like material
            }
        }
    }
}
```

### Pros
- Very flexible
- No inheritance or interface overhead
- Factory methods provide nice API
- Can create custom materials easily

### Cons
- May be harder to debug
- Serialization is more difficult
- Less type safety
- May be less readable for complex materials

## Performance Considerations

1. **Memory Usage**:
   - Data class approach uses the least memory
   - Component-based approaches may use more memory due to object overhead
   - Interface approaches have moderate memory usage

2. **CPU Performance**:
   - Virtual method calls in interfaces have some overhead
   - Many small objects can impact cache locality
   - Functional approaches may have lambda capture overhead

3. **Optimization Opportunities**:
   - Sealed interfaces allow for compiler optimizations
   - Enum-based dispatch can be very efficient
   - Inlining can help with functional approaches

## Recommendation

Based on the requirements for flexibility, maintainability, and performance, I recommend **Alternative 2: Sealed Interface Approach** for the following reasons:

1. It provides a good balance between flexibility and type safety
2. It allows for different material types with their own specific properties
3. The sealed nature enables compiler optimizations and exhaustive pattern matching
4. It's easy to extend with new material types within the module
5. It provides a clear API for users of the ray tracer

For maximum flexibility at the cost of some complexity, **Alternative 3: Composition with Material Components** would be the best choice, especially if you anticipate needing materials with multiple behaviors that can be mixed and matched.

If performance is the absolute priority, the current data class approach or **Alternative 4: Enum-based Material Types** would be more efficient, though less flexible.