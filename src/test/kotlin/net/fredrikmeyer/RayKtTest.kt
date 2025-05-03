package net.fredrikmeyer

import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Sphere
import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RayKtTest {
    @Test
    fun `point on ray`() {
        val ray = Ray(origin = Point3D(0f, 0f, 2f), direction = Vector3D(0f, 0f, -1f))

        val point = ray.pointOnRay(1f)
        assertThat(point).isEqualTo(
            Point3D(0f, 0f, 1f)
        )
    }

    @Test
    fun `fsdf f `() {
        val ray = Ray(origin = Point3D(0f, 0f, 2f), direction = Vector3D(0f, 0f, -1f))
        val sphere = Sphere(center = Point3D(0f, 0f, 0f), radius = 1f)

        val res = sphere.intersect(ray)

        assertThat(res).isNotNull()

        assertThat(res).isEqualTo(1.0f)
    }
}