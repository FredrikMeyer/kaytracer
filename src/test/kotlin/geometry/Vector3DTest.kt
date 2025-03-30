package geometry

import net.fredrikmeyer.geometry.Vector3D
import net.fredrikmeyer.geometry.norm
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Vector3DTest {
    @Test
    fun `normalize vector`() {
        val v = Vector3D(1f, 2f, 3f)
        val res = v.normalize()

        assertThat(res.norm()).isCloseTo(1f, Offset.offset(0.00001f))
    }
}