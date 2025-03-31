package geometry

import net.fredrikmeyer.geometry.Vector3D
import net.fredrikmeyer.geometry.norm
import net.fredrikmeyer.geometry.times
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class Vector3DTest {
    @Test
    fun `normalize vector`() {
        val v = Vector3D(1f, 2f, 3f)
        val res = v.normalize()

        assertThat(res.norm()).isCloseTo(1f, Offset.offset(0.00001f))
    }

    @Test
    fun `cross product`() {
        val res = Vector3D.X cross Vector3D.Y

        assertThat(Vector3D.X cross Vector3D.Y).isEqualTo(Vector3D.Z)
        assertThat(Vector3D.Y cross Vector3D.X).satisfies(Consumer {
            assertThat(it.x).isEqualTo(0f)
            assertThat(it.y).isEqualTo(0f)
            assertThat(it.z).isEqualTo(-1f)
        })
        assertThat(Vector3D.Z cross Vector3D.Y).satisfies(Consumer {
            assertThat(it.x).isEqualTo(-1f)
            assertThat(it.y).isEqualTo(0f)
            assertThat(it.z).isEqualTo(0f)
        })
    }
}