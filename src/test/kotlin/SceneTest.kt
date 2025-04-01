import net.fredrikmeyer.Color
import net.fredrikmeyer.Ray
import net.fredrikmeyer.geometry.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith


/**
 * Tests for the Scene DSL.
 *
 * Note: The DSL now uses the @RayTracerDsl annotation to improve type safety.
 * This prevents accidental access to outer builder scopes from inner builder scopes.
 *
 * For example, the compiler would now reject the following code:
 * ```
 * scene {
 *     objects {
 *         surface {
 *             // Error: Cannot access 'objects' in this scope
 *             objects { }
 *
 *             // Error: Cannot access 'surface' in this scope
 *             surface { }
 *         }
 *     }
 * }
 * ```
 */
class SceneTest {
    @Test
    fun testSceneDSL() {
        val scene = scene {
            surface {
                geometry = Sphere(Point3D(0f, 0f, 0f), 1f)
                material = material {
                    color = Color.RED
                }
            }
            surface {
                geometry = Sphere(Point3D(0f, 0f, 0f), 1f)
                material = material {
                    color = Color.RED
                }
            }
        }

        assertThat(scene.numberOfSurfaces()).isEqualTo(2)

        // Test the scene by creating a ray and checking if it hits anything
        val ray = Ray(Point3D(0f, 0f, -5f), Vector3D(0f, 0f, 1f))
        val hit = scene.hit(ray)

        assertNotNull(hit, "Ray should hit the sphere")
        assertEquals(4f, hit.distance, 0.001f, "Ray should hit at distance 4")
        assertEquals(Color.RED, hit.surface.material.color, "Hit surface should have red color")
    }
}
