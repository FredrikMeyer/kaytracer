import net.fredrikmeyer.*
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

        val res = intersectRaySphere(ray, sphere)

        assertThat(res).isNotNull()
        assertThat(res).isEqualTo(1f)

        val point = ray.pointOnRay(res!!)
        assertThat(point).isEqualTo(
            Point3D(0f, 0f, 1f)
        )
    }
}