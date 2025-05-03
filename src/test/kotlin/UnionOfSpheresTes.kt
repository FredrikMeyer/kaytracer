import net.fredrikmeyer.Ray
//import net.fredrikmeyer.UnionOfSpheres
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Sphere
import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test

//class UnionOfSpheresTest {
//    @Test
//    fun `intersect union of spheres`() {
//        val u = UnionOfSpheres(
//            Sphere(center = Point3D(0f, 0f, 0f), radius = 1f),
//            Sphere(center = Point3D(2f, 0f, 0f), radius = 1f)
//        )
//
//        val ray = Ray(origin = Point3D(0f, -2f, 0f), direction = Vector3D(0f, 1f, 0f))
//        val intersection = u.geometry.intersect(ray)
//
//        assertThat(intersection).isNotNull()
//
//        val p = ray.pointOnRay(intersection!!)
//        assertThat(p.x).isCloseTo(0f, Percentage.withPercentage(0.001))
//        assertThat(p.y).isCloseTo(-1f, Percentage.withPercentage(0.001))
//        assertThat(p.z).isCloseTo(0f, Percentage.withPercentage(0.001))
//
//        val normal = u.geometry.normalAtPoint(p)
//        assertThat(normal.x).isCloseTo(0f, Percentage.withPercentage(0.001))
//        assertThat(normal.y).isCloseTo(-1f, Percentage.withPercentage(0.001))
//        assertThat(normal.z).isCloseTo(0f, Percentage.withPercentage(0.001))
//    }
//}