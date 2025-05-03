package net.fredrikmeyer.geometry


import net.fredrikmeyer.Ray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlaneTest {
    @Test
    fun `intersect plane`() {
        val plane = Plane(
            point = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f)
        )

        val ray = Ray(origin = Point3D(0f, 0f, 1f), direction = Vector3D(0f, 0f, -1f))

        val point = ray.pointOnRay(plane.intersect(ray)!!)
        assertThat(point).isEqualTo(Point3D(0f, 0f, 0f))
    }
}