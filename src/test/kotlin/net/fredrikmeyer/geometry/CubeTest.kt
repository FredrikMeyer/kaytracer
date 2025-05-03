package net.fredrikmeyer.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CubeTest {
    @Test
    fun `normal at point on positive x face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the positive x face
        val point = Point3D(1f, 0f, 0f)

        // The normal should be in the positive x direction
        val expectedNormal = Vector3D(1f, 0f, 0f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point on negative x face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the negative x face
        val point = Point3D(-1f, 0f, 0f)

        // The normal should be in the negative x direction
        val expectedNormal = Vector3D(-1f, 0f, 0f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point on positive y face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the positive y face
        val point = Point3D(0f, 1f, 0f)

        // The normal should be in the positive y direction
        val expectedNormal = Vector3D(0f, 1f, 0f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point on negative y face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the negative y face
        val point = Point3D(0f, -1f, 0f)

        // The normal should be in the negative y direction
        val expectedNormal = Vector3D(0f, -1f, 0f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point on positive z face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the positive z face
        val point = Point3D(0f, 0f, 1f)

        // The normal should be in the positive z direction
        val expectedNormal = Vector3D(0f, 0f, 1f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point on negative z face`() {
        // Create a cube with corners at (-1, -1, -1) and (1, 1, 1)
        val cube = Cube(
            cornerNear = Point3D(-1f, -1f, -1f),
            cornerFar = Point3D(1f, 1f, 1f)
        )

        // Test point on the negative z face
        val point = Point3D(0f, 0f, -1f)

        // The normal should be in the negative z direction
        val expectedNormal = Vector3D(0f, 0f, -1f)

        assertThat(cube.normalAtPoint(point)).isEqualTo(expectedNormal)
    }

    @Test
    fun `normal at point is normalized`() {
        // Create a non-symmetric cube to test normalization
        val cube = Cube(
            cornerNear = Point3D(-1f, -2f, -3f),
            cornerFar = Point3D(2f, 3f, 4f)
        )

        // Test points on each face
        val points = listOf(
            Point3D(2f, 0f, 0f),   // positive x face
            Point3D(-1f, 0f, 0f),  // negative x face
            Point3D(0f, 3f, 0f),   // positive y face
            Point3D(0f, -2f, 0f),  // negative y face
            Point3D(0f, 0f, 4f),   // positive z face
            Point3D(0f, 0f, -3f)   // negative z face
        )

        // All normals should be unit vectors
        for (point in points) {
            val normal = cube.normalAtPoint(point)
            assertThat(normal.norm()).isEqualTo(1.0f)
        }
    }
}