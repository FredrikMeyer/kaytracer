package geometry

import net.fredrikmeyer.Interval
import net.fredrikmeyer.Ray
import net.fredrikmeyer.geometry.Point3D
import net.fredrikmeyer.geometry.Square
import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SquareTest {
    @Test
    fun `intersect square - hit`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // Create a ray starting at (0, 0, 1) pointing in the negative z direction
        // This should hit the center of the square
        val ray = Ray(origin = Point3D(0f, 0f, 1f), direction = Vector3D(0f, 0f, -1f))

        // The intersection parameter should be 1.0f
        assertThat(square.intersect(ray)).isEqualTo(1.0f)
    }

    @Test
    fun `intersect square - hit near edge`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // Create a ray starting at (0.9, 0.9, 1) pointing in the negative z direction
        // This should hit near the edge of the square
        val ray = Ray(origin = Point3D(0.9f, 0.9f, 1f), direction = Vector3D(0f, 0f, -1f))

        // The intersection parameter should be 1.0f
        assertThat(square.intersect(ray)).isEqualTo(1.0f)
    }

    @Test
    fun `intersect square - miss outside square`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // Create a ray starting at (1.1, 1.1, 1) pointing in the negative z direction
        // This should miss the square (outside the bounds)
        val ray = Ray(origin = Point3D(1.1f, 1.1f, 1f), direction = Vector3D(0f, 0f, -1f))

        // The intersection should be null
        assertThat(square.intersect(ray)).isNull()
    }

    @Test
    fun `intersect square - miss parallel`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // Create a ray starting at (0, 0, 1) pointing in the positive x direction
        // This should miss the square (parallel to the square)
        val ray = Ray(origin = Point3D(0f, 0f, 1f), direction = Vector3D(1f, 0f, 0f))

        // The intersection should be null
        assertThat(square.intersect(ray)).isNull()
    }

    @Test
    fun `intersect square - miss outside interval`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // Create a ray starting at (0, 0, 1) pointing in the negative z direction
        // This should hit the center of the square
        val ray = Ray(origin = Point3D(0f, 0f, 1f), direction = Vector3D(0f, 0f, -1f))

        // Create an interval that doesn't include the intersection parameter
        val interval = Interval(2f, 3f)

        // The intersection should be null
        assertThat(square.intersect(ray, interval)).isNull()
    }

    @Test
    fun `normal at point`() {
        // Create a square centered at the origin, with normal in the positive z direction, and size 2
        val square = Square(
            center = Point3D(0f, 0f, 0f),
            normal = Vector3D(0f, 0f, 1f),
            size = 2f
        )

        // The normal at any point should be the normalized normal vector
        assertThat(square.normalAtPoint(Point3D(0f, 0f, 0f))).isEqualTo(Vector3D(0f, 0f, 1f))
    }
}