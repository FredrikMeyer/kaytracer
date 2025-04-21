package geometry

import net.fredrikmeyer.Ray
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Triangle
import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TriangleTest {
    @Test
    fun `intersect triangle`() {
        val ray = Ray(origin = Point3D(0f, 0f, 0f), direction = Vector3D(1f, 1f, 1f).normalize())

        val triangle = Triangle(Point3D(1f, 0f, 0f), Point3D(0f, 1f, 0f), Point3D(0f, 0f, 1f))

        val t = triangle.intersect(ray)

        assertThat(t).isNotNull()

        val point = ray.pointOnRay(t!!)
        assertThat(point).isEqualTo(Point3D(0.3333333f, 0.3333333f, 0.3333333f))
    }

    @Test
    fun `isOnObject returns true for point on triangle`() {
        // Create a triangle in the xy-plane
        val triangle = Triangle(
            a = Point3D(0f, 0f, 0f),
            b = Point3D(1f, 0f, 0f),
            c = Point3D(0f, 1f, 0f)
        )

        // Test a point on the triangle (barycentric coordinates)
        val pointOnTriangle = Point3D(0.25f, 0.25f, 0f)
        assertThat(triangle.isOnObject(pointOnTriangle)).isTrue()

        // Test a point on the edge of the triangle
        val pointOnEdge = Point3D(0.5f, 0.5f, 0f)
        assertThat(triangle.isOnObject(pointOnEdge)).isTrue()

        // Test a point on a vertex of the triangle
        val pointOnVertex = Point3D(0f, 0f, 0f)
        assertThat(triangle.isOnObject(pointOnVertex)).isTrue()
    }

    @Test
    fun `isOnObject returns false for point not on triangle`() {
        // Create a triangle in the xy-plane
        val triangle = Triangle(
            a = Point3D(0f, 0f, 0f),
            b = Point3D(1f, 0f, 0f),
            c = Point3D(0f, 1f, 0f)
        )

        // Test a point not on the plane of the triangle
        val pointNotOnPlane = Point3D(0.25f, 0.25f, 0.1f)
        assertThat(triangle.isOnObject(pointNotOnPlane)).isFalse()

        // Test a point on the plane but outside the triangle
        val pointOutsideTriangle = Point3D(2f, 2f, 0f)
        assertThat(triangle.isOnObject(pointOutsideTriangle)).isFalse()
    }
}