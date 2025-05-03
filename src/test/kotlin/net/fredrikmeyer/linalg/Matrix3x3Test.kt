package net.fredrikmeyer.linalg


import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Matrix3x3Test {
    @Test
    fun `transpose transpose is identity`() {
        val m = Matrix3x3(Vector3D.random(), Vector3D.random(), Vector3D.random())

        assertEquals(m, m.transpose().transpose())
    }

    @Test
    fun `multiply by identity`() {
        val m = Matrix3x3.identity()
        val n = Matrix3x3(Vector3D.random(), Vector3D.random(), Vector3D.random())

        val r = m * n

        assertThat(r).isEqualTo(n)
    }

    @Test
    fun `determinant of identity`() {
        val m = Matrix3x3.identity()

        val r = m.determinant()

        assertThat(r).isEqualTo(1.0f)
    }

    @Test
    fun `determinant transposed is equal to determinant`() {
        val m = Matrix3x3(Vector3D.random(), Vector3D.random(), Vector3D.random())

        val mt = m.transpose()

        assertThat(m.determinant()).isEqualTo(mt.determinant(), Offset.offset(0.000001f))
    }
}