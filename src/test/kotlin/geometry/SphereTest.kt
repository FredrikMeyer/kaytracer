package geometry

import net.fredrikmeyer.Ray
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Sphere
import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class SphereTest {
    @Test
    fun `intersect sphere`() {
        val ray = Ray(origin = Point3D(0f, 0f, 0f), direction = Vector3D(1f, 1f, 1f).normalize())

        val sphere = Sphere(
            center = Point3D(1f, 1f, 1f),
            radius = 1f
        )

        val point = sphere.intersect(ray)

        assertThat(point).isNotNull()
        assertThat(point).isEqualTo((sqrt(3.0) - 1).toFloat())
    }
}